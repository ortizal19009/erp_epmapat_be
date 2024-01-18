// package com.epmapat.erp_epmapat.modelo.contabilidad;
package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Table(name = "beneficiarios")

public class Beneficiarios {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idbene;
	private String codben;
	private String nomben;
	private String tpidben;
	private String rucben;
	private String ciben;
	private String tlfben;
	private String dirben;
	private String mailben;
	private Long tpcueben;
	private String cuebanben;
	private String foto;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idgrupo")
	private Gruposbene idgrupo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idifinan")
	private Ifinan idifinan;
   
	private Long swconsufin;
	private Long usucrea;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "feccrea")
	private Date feccrea;
	private Long usumodi;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "fecmodi")
	private Date fecmodi;

	public Beneficiarios() {
		super();
	}

	public Beneficiarios(Long idbene, String codben, String nomben, String tpidben, String rucben, String ciben,
			String tlfben, String dirben, String mailben, Long tpcueben,String cuebanben, String foto, Gruposbene idgrupo,
			Ifinan idifinan, Long swconsufin, Long usucrea, Date feccrea, Long usumodi, Date fecmodi) {
		super();
		this.idbene = idbene;
		this.codben = codben;
		this.nomben = nomben;
		this.tpidben = tpidben;
		this.rucben = rucben;
		this.ciben = ciben;
		this.tlfben = tlfben;
		this.dirben = dirben;
		this.mailben = mailben;
		this.tpcueben = tpcueben;
		this.setCuebanben(cuebanben);
		this.foto = foto;
		this.idgrupo = idgrupo;
		this.idifinan = idifinan;
		this.swconsufin = swconsufin;
		this.usucrea = usucrea;
		this.feccrea = feccrea;
		this.usumodi = usumodi;
		this.fecmodi = fecmodi;
	}

	public Long getIdbene() {
		return idbene;
	}
	public void setIdbene(Long idbene) {
		this.idbene = idbene;
	}
	public String getCodben() {
		return codben;
	}
	public void setCodben(String codben) {
		this.codben = codben;
	}
	public String getNomben() {
		return nomben;
	}
	public void setNomben(String nomben) {
		this.nomben = nomben;
	}
	public String getTpidben() {
		return tpidben;
	}
	public void setTpidben(String tpidben) {
		this.tpidben = tpidben;
	}
	public String getRucben() {
		return rucben;
	}
	public void setRucben(String rucben) {
		this.rucben = rucben;
	}
	public String getCiben() {
		return ciben;
	}
	public void setCiben(String ciben) {
		this.ciben = ciben;
	}
	public String getTlfben() {
		return tlfben;
	}
	public void setTlfben(String tlfben) {
		this.tlfben = tlfben;
	}
	public String getDirben() {
		return dirben;
	}
	public void setDirben(String dirben) {
		this.dirben = dirben;
	}
	public String getMailben() {
		return mailben;
	}
	public void setMailben(String mailben) {
		this.mailben = mailben;
	}
	public Long getTpcueben() {
		return tpcueben;
	}
	public void setTpcueben(Long tpcueben) {
		this.tpcueben = tpcueben;
	}
	public String getFoto() {
		return foto;
	}
	public void setFoto(String foto) {
		this.foto = foto;
	}
	public Gruposbene getIdgrupo() {
		return idgrupo;
	}
	public void setIdgrupo(Gruposbene idgrupo) {
		this.idgrupo = idgrupo;
	}
	public Ifinan getIdifinan() {
		return idifinan;
	}
	public void setIdifinan(Ifinan idifinan) {
		this.idifinan = idifinan;
	}
	public Long getSwconsufin() {
		return swconsufin;
	}
	public void setSwconsufin(Long swconsufin) {
		this.swconsufin = swconsufin;
	}
	public Long getUsucrea() {
		return usucrea;
	}
	public void setUsucrea(Long usucrea) {
		this.usucrea = usucrea;
	}
	public Date getFeccrea() {
		return feccrea;
	}
	public void setFeccrea(Date feccrea) {
		this.feccrea = feccrea;
	}
	public Long getUsumodi() {
		return usumodi;
	}
	public void setUsumodi(Long usumodi) {
		this.usumodi = usumodi;
	}
	public Date getFecmodi() {
		return fecmodi;
	}

	public void setFecmodi(Date fecmodi) {
		this.fecmodi = fecmodi;
	}

	public String getCuebanben() {
		return cuebanben;
	}

	public void setCuebanben(String cuebanben) {
		this.cuebanben = cuebanben;
	}

}
