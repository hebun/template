package freela.util;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import freela.util.Sql.Delete;
import freela.util.Sql.Insert;
import freela.util.Sql.Select;
import freela.util.Sql.Update;
import static freela.util.Sql.*;

public class TestSql {

	public void back() {

		int ret = new Insert("test").add("test", "blblba").run();
		assertTrue(ret > 0);

		ret = new Insert("test").add("test", "pppp").run();
		assertTrue(ret > 0);

		ret = new Update("test").add("test", "update").where("id", ret).run();
		assertTrue(ret > 0);
		ret = new Update("test").add("test", "updatez").where("id", 3).run();
		assertTrue(ret > 0);

		ret = new Update("test").add("test", "updatev").where("id", 3).run();
		assertTrue(ret > 0);

		ret = new Delete("test").where("id", ret).run();

		List<Map<String, String>> table = ((Select) new Select().from("test")
				.where("id>", "7")).getTable();

		assertTrue(ret > 0);
		Select sql = new Select().from("product").innerJoin("user")
				.on("p.userid", "u.id");
		String string = sql.get();

		List<Map<String, String>> tablex = sql.getTable();

		System.out.println(tablex);

		assertEquals(
				"select * from `product` as p inner join user as u on p.userid=u.id",
				string);
	}

	@Test
	public void test() {
		String id = "141";
		Select select = new Select().from("user").where("id", "10").doNotUsePrepared();
		String string = select.get();
		System.out.println(string);

	}

	private void bla(String name) {
		// TODO Auto-generated method stub

	}

	private void bla(Class<?> class1) {
		// TODO Auto-generated method stub

	}
}
