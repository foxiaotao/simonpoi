package simon.demo.core.bean;

import java.util.Date;

import simon.demo.core.util.simonexcel.MapperCell;

/**
 * @author xiaotao
 *
 */
public class PartyMember {
	@MapperCell(cellName="姓名",order=1)
	private String name;
	@MapperCell(cellName="所在党支部",order=2)
	private String zhibu;
	@MapperCell(cellName="公民身份证号",order=3)
	private String idNo;
	@MapperCell(cellName="性别",order=4)
	private String gender;
	@MapperCell(cellName="民族",order=5)
	private String nation;
	@MapperCell(cellName="出生日期",order=6)
	private String birthday; 
	@MapperCell(cellName="学历",order=7)
	private String enducational; 
	@MapperCell(cellName="人员类别",order=8)
	private String peoType;//", "人员类别"); 
	@MapperCell(cellName="加入党组织日期",order=9)
	private String inDate;//, "加入党组织日期"); 
	@MapperCell(cellName="转为正式党员日期",order=10)
	private String turnDate;//", "转为正式党员日期"); 
	@MapperCell(cellName="工作岗位",order=11)
	private String job;//", "工作岗位"); 
	@MapperCell(cellName="手机号",order=12)
	private String phone;//", "手机号"); 
	@MapperCell(cellName="固定电话",order=13)
	private String homeTel;//", "固定电话");
	@MapperCell(cellName="家庭住址",order=14)
	private String address;//", "家庭住址");
	@MapperCell(cellName="党籍状态",order=15)
	private String partyStatus;//", "党籍状态"); 
	@MapperCell(cellName="是否为失联党员",order=16)
	private String isRelation;//", "是否为失联党员"); 
	@MapperCell(cellName="失去联系日期",order=17)
	private String noRelationDate;//", "失去联系日期"); 
	@MapperCell(cellName="是否为流动党员",order=18)
	private String isFlowParty;//", "是否为流动党员"); 
	@MapperCell(cellName="外出流向",order=19)
	private String flowAddr;//", "外出流向"); 
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getZhibu() {
		return zhibu;
	}
	public void setZhibu(String zhibu) {
		this.zhibu = zhibu;
	}
	public String getIdNo() {
		return idNo;
	}
	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getNation() {
		return nation;
	}
	public void setNation(String nation) {
		this.nation = nation;
	}
	public String getEnducational() {
		return enducational;
	}
	public void setEnducational(String enducational) {
		this.enducational = enducational;
	}
	public String getPeoType() {
		return peoType;
	}
	public void setPeoType(String peoType) {
		this.peoType = peoType;
	}
	public String getJob() {
		return job;
	}
	public void setJob(String job) {
		this.job = job;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getHomeTel() {
		return homeTel;
	}
	public void setHomeTel(String homeTel) {
		this.homeTel = homeTel;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPartyStatus() {
		return partyStatus;
	}
	public void setPartyStatus(String partyStatus) {
		this.partyStatus = partyStatus;
	}
	public String getIsRelation() {
		return isRelation;
	}
	public void setIsRelation(String isRelation) {
		this.isRelation = isRelation;
	}
	public String getIsFlowParty() {
		return isFlowParty;
	}
	public void setIsFlowParty(String isFlowParty) {
		this.isFlowParty = isFlowParty;
	}
	public String getFlowAddr() {
		return flowAddr;
	}
	public void setFlowAddr(String flowAddr) {
		this.flowAddr = flowAddr;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getInDate() {
		return inDate;
	}
	public void setInDate(String inDate) {
		this.inDate = inDate;
	}
	public String getTurnDate() {
		return turnDate;
	}
	public void setTurnDate(String turnDate) {
		this.turnDate = turnDate;
	}
	public String getNoRelationDate() {
		return noRelationDate;
	}
	public void setNoRelationDate(String noRelationDate) {
		this.noRelationDate = noRelationDate;
	}
	
}
