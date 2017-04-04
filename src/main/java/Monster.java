import org.sql2o.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.sql.Timestamp;
import java.util.Date;
import java.text.DateFormat;

public abstract class Monster {
  public int id;
  public String name;
  public String type;
  public int personId;
  public int foodLevel;
  public int sleepLevel;
  public int playLevel;
  public Timestamp birthday;
  public Timestamp lastSlept;
  public Timestamp lastAte;
  public Timestamp lastPlayed;
  public Timer timer;

  public static final int MAX_FOOD_LEVEL = 3;
  public static final int MAX_SLEEP_LEVEL = 8;
  public static final int MAX_PLAY_LEVEL = 12;
  public static final int MIN_ALL_LEVELS = 0;

  public boolean isAlive() {
    if (foodLevel <= MIN_ALL_LEVELS ||
    playLevel <= MIN_ALL_LEVELS ||
    sleepLevel <= MIN_ALL_LEVELS) {
      return false;
    }
    return true;
  }

  public void depleteLevels(){
    if (isAlive()){
      playLevel--;
      foodLevel--;
      sleepLevel--;
    }
  }

  public void play(){
    if (playLevel >= MAX_PLAY_LEVEL){
      throw new UnsupportedOperationException("You cannot play with monster anymore!");
    }
    try(Connection con = DB.sql2o.open()) {
      String sql = "UPDATE monsters SET lastplayed = now() WHERE id = :id";
      con.createQuery(sql)
        .addParameter("id", id)
        .executeUpdate();
      }
    playLevel++;
  }

  public void sleep(){
    if (sleepLevel >= MAX_SLEEP_LEVEL){
      throw new UnsupportedOperationException("You cannot make your monster sleep anymore!");
    }
    try(Connection con = DB.sql2o.open()) {
      String sql = "UPDATE monsters SET lastslept = now() WHERE id = :id";
      con.createQuery(sql)
        .addParameter("id", id)
        .executeUpdate();
      }
    sleepLevel++;
  }


  public void feed(){
    if (foodLevel >= MAX_FOOD_LEVEL){
      throw new UnsupportedOperationException("You cannot feed your monster anymore!");
    }
    try(Connection con = DB.sql2o.open()) {
      String sql = "UPDATE monsters SET lastate = now() WHERE id = :id";
      con.createQuery(sql)
        .addParameter("id", id)
        .executeUpdate();
      }
    foodLevel++;
  }

  public String getName(){
    return name;
  }

  public int getPersonId(){
    return personId;
  }

  public int getFoodLevel(){
    return foodLevel;
  }
  public Timestamp getLastAte(){
    return lastAte;
  }

  public int getId(){
    return id;
  }

  public int getPlayLevel(){
    return playLevel;
  }
  public Timestamp getLastPlayed(){
    return lastPlayed;
  }

  public int getSleepLevel(){
    return sleepLevel;
  }

  public Timestamp getLastSlept(){
    return lastSlept;
  }
  public Timestamp getBirthday(){
    return birthday;
  }

  @Override
  public boolean equals(Object otherMonster){
    if (!(otherMonster instanceof Monster)) {
      return false;
    } else {
      Monster newMonster = (Monster) otherMonster;
      return this.getName().equals(newMonster.getName()) &&
             this.getPersonId() == newMonster.getPersonId();
    }
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO monsters (name, personId, birthday, type) VALUES (:name, :personId, now(), :type)";
      this.id = (int) con.createQuery(sql, true)
        .addParameter("name", this.name)
        .addParameter("personId", this.personId)
        .addParameter("type", this.type)
        .executeUpdate()
        .getKey();
    }
  }
//remove THIS ALL FUNCTION BECAUSE IT IS IN FIRE AND WATER MONSTERS
  // public static List<Monster> all() {
  //   String sql = "SELECT * FROM monsters;";
  //   try(Connection con = DB.sql2o.open()) {
  //     return con.createQuery(sql).executeAndFetch(Monster.class);
  //   }
  // }
  //
  // public static Monster find(int id) {
  //   try(Connection con = DB.sql2o.open()) {
  //     String sql = "SELECT * FROM monsters where id=:id";
  //     Monster monster = con.createQuery(sql)
  //       .addParameter("id", id)
  //       .executeAndFetchFirst(Monster.class);
  //     return monster;
  //   }
  // }

  public void startTimer(){
      Monster currentMonster = this;
      TimerTask timerTask = new TimerTask(){
        @Override
        public void run() {
          if (currentMonster.isAlive() == false){
            cancel();
          }
          depleteLevels();
        }
      };
      this.timer.schedule(timerTask, 0, 600);
    }



}
