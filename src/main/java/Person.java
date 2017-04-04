import java.util.ArrayList;
import java.util.List;
import org.sql2o.*;

public class Person {
  private String name;
  private String email;
  private int id;

  public Person(String name, String email) {
    this.name = name;
    this.email = email;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public int getId() {
    return id;
  }

  @Override
  public boolean equals(Object otherPerson){
    if (!(otherPerson instanceof Person)) {
      return false;
    } else {
      Person newPerson = (Person) otherPerson;
      return this.getName().equals(newPerson.getName()) &&
             this.getEmail().equals(newPerson.getEmail());
    }
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO persons (name, email) VALUES (:name, :email)";
      this.id = (int) con.createQuery(sql, true)
        .addParameter("name", this.name)
        .addParameter("email", this.email)
        .executeUpdate()
        .getKey();
    }
  }

  public static List<Person> all() {
    String sql = "SELECT id, name, email FROM persons";
    try(Connection con = DB.sql2o.open()) {
     return con.createQuery(sql).executeAndFetch(Person.class);
    }
  }

  public List<Monster> getMonsters() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM monsters where personId=:id";
      return con.createQuery(sql)
        .addParameter("id", this.id)
        .executeAndFetch(Monster.class);
    }
  }

  public List<Community> getCommunities() {
    try(Connection con = DB.sql2o.open()){
      String joinQuery = "SELECT community_id FROM communities_persons WHERE person_id = :person_id";
      List<Integer> communityIds = con.createQuery(joinQuery)
        .addParameter("person_id", this.getId())
        .executeAndFetch(Integer.class);

      List<Community> communities = new ArrayList<Community>();

      for (Integer communityId : communityIds) {
        String communityQuery = "SELECT * FROM communities WHERE id = :communityId";
        Community community = con.createQuery(communityQuery)
          .addParameter("communityId", communityId)
          .executeAndFetchFirst(Community.class);
        communities.add(community);
      }
      return communities;
    }
  }

  public void leaveCommunity(Community community){
    try(Connection con = DB.sql2o.open()){
      String joinRemovalQuery = "DELETE FROM communities_persons WHERE community_id = :communityId AND person_id = :personId;";
      con.createQuery(joinRemovalQuery)
        .addParameter("communityId", community.getId())
        .addParameter("personId", this.getId())
        .executeUpdate();
    }
  }

}
