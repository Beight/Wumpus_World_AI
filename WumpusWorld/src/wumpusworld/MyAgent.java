package wumpusworld;

/**
 * Contans starting code for creating your own Wumpus World agent.
 * Currently the agent only make a random decision each turn.
 * 
 * @author Johan Hagelbäck
 */

public class MyAgent implements Agent
{
    private World w;
    
    /**
     * Creates a new instance of your solver agent.
     * 
     * @param world Current world state 
     */
    public MyAgent(World world)
    {
        w = world;
    }
    
    /**
     * Asks your solver agent to execute an action.
     */
    public void doAction()
    {
        //Location of the player
        int cX = w.getPlayerX();
        int cY = w.getPlayerY();
        int prevX = w.getPrevPlayerPositionX();
        int prevY = w.getPrevPlayerPositionY();
        
        //Basic action:
        //Grab Gold if we can.
        if (w.hasGlitter(cX, cY))
        {
            w.doAction(World.A_GRAB);
            return;
        }
        
        //Basic action:
        //We are in a pit. Climb up.
        if (w.isInPit())
        {
            w.doAction(World.A_CLIMB);
            return;
        }
        
        //Test the environment
        if (w.hasBreeze(cX, cY))
        {
            System.out.println("I am in a Breeze");
        }
        if (w.hasStench(cX, cY))
        {
            System.out.println("I am in a Stench");
        }
        if (w.hasPit(cX, cY))
        {
            System.out.println("I am in a Pit");
        }
        if (w.getDirection() == World.DIR_RIGHT)
        {
            System.out.println("I am facing Right");
        }
        if (w.getDirection() == World.DIR_LEFT)
        {
            System.out.println("I am facing Left");
        }
        if (w.getDirection() == World.DIR_UP)
        {
            System.out.println("I am facing Up");
        }
        if (w.getDirection() == World.DIR_DOWN)
        {
            System.out.println("I am facing Down");
        }
        
        if(w.hasBreeze(cX, cY))
        {
            if(w.isValidPosition(cX + 1, cY))
                if(w.isUnknown(cX + 1, cY))
                    return;
            if(w.isValidPosition(cX - 1, cY))
                if(w.isUnknown(cX - 1, cY))
                    return;
            if(w.isValidPosition(cX, cY + 1))
                if(w.isUnknown(cX, cY + 1))
                    return;
            if(w.isValidPosition(cX, cY - 1))
                if(w.isUnknown(cX, cY - 1))
                    return;
        }
        
        
        if(explore(cX, cY + 1, World.DIR_UP))
            return;
        
        if(explore(cX, cY - 1, World.DIR_DOWN))
            return;
        
        if(explore(cX + 1, cY, World.DIR_RIGHT))
            return;
        
        if(explore(cX - 1, cY, World.DIR_LEFT))
            return;
        
        
        //Random move actions
        int rnd = (int)(Math.random() * 5);
        if (rnd == 0) 
        {
            w.doAction(World.A_TURN_LEFT);
            return;
        }
        if (rnd == 1)
        {
            w.doAction(World.A_TURN_RIGHT);
            return;
        }
        if (rnd >= 2)
        {
            w.doAction(World.A_MOVE);
            return;
        }
    }
    private boolean explore(int p_X, int p_Y, int p_Dir)
    {
        if(w.isValidPosition(p_X, p_Y))
            if(w.isUnknown(p_X, p_Y))
                if(move(p_Dir))
                    return true;
        
        return false;
    }
    
    private boolean move(int p_Dir)
    {
        if(w.getDirection() == p_Dir)
        {
            w.doAction(World.A_MOVE);
            return true;
        }
        else
        {
           switch(p_Dir)
           {
                case World.DIR_UP:
                    switch(w.getDirection())
                    {
                         case World.DIR_LEFT:
                            w.doAction(World.A_TURN_RIGHT);
                            return true;
                        case World.DIR_RIGHT:
                            w.doAction(World.A_TURN_LEFT);
                            return true;
                        default:
                            w.doAction(World.A_TURN_LEFT);
                            return true;
                    }
                case World.DIR_DOWN:
                    switch(w.getDirection())
                    {
                        case World.DIR_LEFT:
                            w.doAction(World.A_TURN_LEFT);
                            return true;
                        case World.DIR_RIGHT:
                            w.doAction(World.A_TURN_RIGHT);
                            return true;
                        default:
                            w.doAction(World.A_TURN_LEFT);
                            return true;
                    }
                case World.DIR_LEFT:
                    switch(w.getDirection())
                    {
                        case World.DIR_UP:
                            w.doAction(World.A_TURN_LEFT);
                            return true;
                        case World.DIR_DOWN:
                            w.doAction(World.A_TURN_RIGHT);
                            return true;
                        default:
                            w.doAction(World.A_TURN_LEFT);
                            return true;
                    }
                case World.DIR_RIGHT:
                    switch(w.getDirection())
                    {
                        case World.DIR_UP:
                            w.doAction(World.A_TURN_RIGHT);
                            return true;
                        case World.DIR_DOWN:
                            w.doAction(World.A_TURN_LEFT);
                            return true;
                        default:
                            w.doAction(World.A_TURN_LEFT);
                            return true;
                    }
           }   
        }
        return false;
    }
    
}



