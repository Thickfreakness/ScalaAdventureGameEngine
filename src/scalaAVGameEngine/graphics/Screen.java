package scalaAVGameEngine.graphics;

import scalaAVGameEngine.io.Input.TouchEvent;
import akka.actor.ActorRef;

public interface Screen {

    public  void update(float deltaTime);

    public  void present(float deltaTime);

    public  void pause();

    public  void resume();

    public  void dispose();
    
    public  void sayThought(String thought);
    
    public  void converse(ActorRef actor, String line);
    
    public  void handleEvent(TouchEvent event);
    
    public  void startConversation(ActorRef act);
    
    public  void endConversation();
}
