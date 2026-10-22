package dao.server.core;

import dao.GameConstants;
import org.jctools.queues.MpscArrayQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

public class GameLoop {

  public static final long TICK_MS = 1000 / GameConstants.TICK_RATE;
  private static final Logger log = LoggerFactory.getLogger(GameLoop.class);
  private final World world = new World();

  private final MpscArrayQueue<Command> mailbox = new MpscArrayQueue<>(4096);

  private final List<GameSystem> systems = new ArrayList<>();

  private volatile boolean running = false;

  public void addSystem(GameSystem system){
    systems.add(system);
  }

  public boolean submit(Command command){
    return mailbox.offer(command);
  }

  public void step(){
    Command command;
    while ((command=mailbox.poll())!=null){
      command.executor(world);
    }
    for(GameSystem system:systems){
      system.update(world);
    }
    world.advanceTick();
  }

  public void run(){
    running = true;
    long tickNano = TICK_MS*1_000_000L;
    long next = System.nanoTime()+tickNano;

    while(running){
      try{
        step();
      } catch (RuntimeException e){
        log.error("loi : ",e);
      }
      long wait = next-System.nanoTime();

      if(wait>0){
        LockSupport.parkNanos(wait);
        next+=tickNano;
      }
      next = System.nanoTime() + tickNano;
    }

  }

  public void stop(){
    running=false;
  }

  public World world(){
    return world;
  }

  public void sum() {

  }
}
