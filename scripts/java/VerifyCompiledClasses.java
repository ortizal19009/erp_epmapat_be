import java.nio.file.*;
import java.util.*;
class VerifyCompiledClasses {
 public static void main(String[] args) throws Exception {
  Path root=Path.of(args[0]);int checked=0,failed=0;
  try(var files=Files.walk(root.resolve("com/epmapat/erp_epmapat"))){
   for(Path file:files.filter(p->p.toString().endsWith(".class")).toList()){
    String name=root.relativize(file).toString().replace('\\','.').replace('/','.').replaceAll("\\.class$","");
    checked++;
    // Eclipse may emit loadable classes whose methods only throw compilation errors.
    String constants=new String(Files.readAllBytes(file),java.nio.charset.StandardCharsets.ISO_8859_1);
    if(constants.contains("Unresolved compilation problem")){
     failed++;if(failed<=12)System.out.println(name+": contiene errores de compilacion del editor");continue;
    }
    try{Class.forName(name,false,VerifyCompiledClasses.class.getClassLoader()).getDeclaredMethods();}
    catch(LinkageError|ClassNotFoundException e){failed++;if(failed<=12)System.out.println(name+": "+e.getClass().getSimpleName()+" "+e.getMessage());}
   }
  }
  System.out.println("Clases verificadas="+checked+"; errores="+failed);
  if(failed>0)System.exit(1);
 }
}
