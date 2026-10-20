"""Verify migrated NC schema locally using rollback-only fixtures. Never migrates or starts the ERP."""
from pathlib import Path
import argparse, os, re, shutil, subprocess

root = Path(__file__).resolve().parents[1]
parser = argparse.ArgumentParser(description=__doc__)
parser.add_argument('--database', required=True, help='Authorized local test database on 127.0.0.1:5432')
parser.add_argument('--environment-file', default='.env.local')
args = parser.parse_args()
if not re.fullmatch(r'[A-Za-z0-9_]+', args.database):
    parser.error('Invalid local database name')
settings = {}
for line in (root / args.environment_file).read_text(encoding='utf-8-sig').splitlines():
    if '=' in line and not line.lstrip().startswith('#'):
        key, value = line.split('=', 1)
        settings[key.strip()] = value.strip().strip(chr(34)).strip(chr(39))
if not settings.get('SPRING_DATASOURCE_USERNAME') or not settings.get('SPRING_DATASOURCE_PASSWORD'):
    parser.error('Configure local database credentials in the environment file; do not paste passwords')
out = root / 'target' / 'nc-uat-20261020'
out.mkdir(parents=True, exist_ok=True)
maven = shutil.which('mvn.cmd') or shutil.which('mvn')
if not maven:
    parser.error('Maven is required')
with (out / 'compilacion.log').open('wb') as log:
    subprocess.run([maven, '-Pnc-tests-isolated', '-DskipTests', 'compile', 'dependency:build-classpath', '-Dmdep.outputFile=' + str(out / 'classpath.txt')], cwd=root, stdout=log, stderr=subprocess.STDOUT, check=True, timeout=180)
env = os.environ.copy()
env.update(NC_UAT_DATABASE=args.database, NC_UAT_USER=settings['SPRING_DATASOURCE_USERNAME'], NC_UAT_PASSWORD=settings['SPRING_DATASOURCE_PASSWORD'])
classpath = (out / 'classpath.txt').read_text().strip() + os.pathsep + str(root / 'target' / 'nc-tests-build' / 'classes')
with (out / 'verificacion.log').open('wb') as log:
    result = subprocess.run(['java', '-Djava.awt.headless=true', '--class-path', classpath, str(root / 'scripts' / 'nc' / 'VerifyNcUatLocal.java')], cwd=root, env=env, stdout=log, stderr=subprocess.STDOUT, timeout=120)
lines = (out / 'verificacion.log').read_text(encoding='utf-8', errors='replace').splitlines()
if result.returncode:
    print('Local UAT failed; transaction rolled back. Review target/nc-uat-20261020/verificacion.log.')
else:
    print('\n'.join(line for line in lines if line.startswith(('APROBADO:', 'ROLLBACK'))))
raise SystemExit(result.returncode)
