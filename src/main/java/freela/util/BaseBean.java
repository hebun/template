package freela.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BaseBean {

	protected List<Map<String, String>> data;
	protected Map<String, String> record;
	protected String table;

	public void loadData() {
		data = Db.selectFrom(table);
	}

	public String delete(String id) {

		Db.prepareInsert("delete from `" + table + "` where id=?",
				Arrays.asList(new String[] { id }));
		return "";
	}

	public void loadData(String column, String value) {

		if(this.table==null){
			throw new RuntimeException("first, set 'table' in baseBean");
		}
		
		data = Db.preparedSelect("select * from `" + table + "` where `"
				+ column + "`=?", Arrays.asList(new String[] { value }));
	}

	public BaseBean() {
	}

	public String getTable() {
	
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public List<Map<String, String>> getData() {
		return data;
	}

	public void setData(List<Map<String, String>> data) {
		this.data = data;
	}

	public Map<String, String> getRecord() {
		return record;
	}

	public void setRecord(Map<String, String> record) {
		this.record = record;
	}

}
