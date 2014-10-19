package wumpusworld;
import java.util.ArrayList;
import java.util.Random;
/**
 * Contans starting code for creating your own Wumpus World agent.
 * Currently the agent only make a random decision each turn.
 * 
 * @author Johan Hagelbäck
 */

public class MyAgent implements Agent
{
    private World w;
    private ArrayList<Position> m_Breezes;
    private ArrayList<Position> m_Stenches;
    private ArrayList<Position> m_Pits; //Can include potetial pits to.
    private ArrayList<Position> m_MoveQueue;
    private Position m_WumpusPos;
    /**
     * Creates a new instance of your solver agent.
     * 
     * @param world Current world state 
     */
    public MyAgent(World world)
    {
        m_Breezes = new ArrayList<>();
        m_Stenches = new ArrayList<>();
        m_Pits = new ArrayList<>();
        m_WumpusPos = new Position(-1, -1);
        m_MoveQueue = new ArrayList<>();
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
        Position pos = new Position(cX, cY);
        int prevX = w.getPrevPlayerPositionX();
        int prevY = w.getPrevPlayerPositionY();
        
        
        
        //Basic action:
        //Grab Gold if we can.
        if (w.hasGlitter(pos))
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
        if (w.hasBreeze(pos))
        {
            addBreeze(pos);
            //if(w.isVisited(cX + 1, cY))
                
                
            //int nrOfBreezes = checkIfPit(new Position(cX, cY));
            
            addPit(new Position(cX + 1, cY));
            addPit(new Position(cX - 1, cY));
            addPit(new Position(cX, cY + 1));
            addPit(new Position(cX, cY - 1));
            System.out.println("I am in a Breeze");
        }
        if (w.hasStench(pos))
        {
            m_Stenches.add(new Position(cX, cY));
            System.out.println("I am in a Stench");
        }
        if (w.hasPit(pos))
        {
            m_Pits.add(new Position(cX, cY));
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
        
        
        
        //TODO: Save number of valid moves and also save the positions for those moves and make a decision on what to do out of those moves.
        
        if(!m_MoveQueue.isEmpty())
            if(Position.equals(m_MoveQueue.get(0), pos))
                m_MoveQueue.remove(0);
            
        if(explore(pos))
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
    private boolean explore(Position p_Pos)
    {
        if(m_MoveQueue.isEmpty())
        {
            ArrayList<Position> availableMoves = new ArrayList<>();

            if(w.isUnknown(p_Pos.up()))
                if(!checkIfPit(p_Pos.up()))
                    availableMoves.add(p_Pos.up());

            if(w.isUnknown(p_Pos.right()))
                if(!checkIfPit(p_Pos.right()))
                    availableMoves.add(p_Pos.right());

            if(w.isUnknown(p_Pos.left()))
                if(!checkIfPit(p_Pos.left()))
                    availableMoves.add(p_Pos.left());

            if(w.isUnknown(p_Pos.down()))
                if(!checkIfPit(p_Pos.down()))
                    availableMoves.add(p_Pos.down());

            if(!availableMoves.isEmpty())
            {
                int rand = randInt(0, availableMoves.size() - 1);
                System.out.println("Getting a new destination");
                m_MoveQueue.add(availableMoves.get(rand));
            }
            else 
            {
                
            }
        }
        
        int dir = calcDir(p_Pos, m_MoveQueue.get(0));
        
        if(move(dir))
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
    
    
    
    private int calcDir(Position p_PlayerPos, Position p_Destination)
    {
        if(p_PlayerPos.X > p_Destination.X)
            return World.DIR_LEFT;
        if(p_PlayerPos.X < p_Destination.X)
            return World.DIR_RIGHT;
        if(p_PlayerPos.Y < p_Destination.Y)
            return World.DIR_UP;
        else
            return World.DIR_DOWN;
    }
    
    private void addBreeze(Position p_Pos)
    {
        boolean saved = false;
        for(Position p : m_Breezes)
            if(Position.equals(p, p_Pos))
                saved = true;

        if(!saved)
            m_Breezes.add(p_Pos);
    }
    
    private void addPit(Position p_Pos)
    {
            if(w.isValidPosition(p_Pos))
            {
                if(w.isUnknown(p_Pos))
                {
                    boolean saved = false;
                    for(Position p : m_Pits)
                        if(Position.equals(p, p_Pos))
                            saved = true;
                    
                    if(!saved)
                        m_Pits.add(p_Pos);
                }
            }
    }
    
    private boolean checkIfPit(Position p_Pos)
    {
        for(Position p : m_Pits)
            if(Position.equals(p, p_Pos))
               return true;
                
        return false;
    }
    
    private int addUnknownPit(Position p_Pos)
    {
                int nrOfBreezes = 1;
        if(w.isValidPosition(p_Pos.DiagonalRightUp()))
            if(w.isVisited(p_Pos.DiagonalRightUp()))
                if(w.hasBreeze(p_Pos.DiagonalRightUp()))
                    nrOfBreezes++;
        
        if(w.isValidPosition(p_Pos.DiagonalRightDown()))
            if(w.isVisited(p_Pos.DiagonalRightDown()))
                if(w.hasBreeze(p_Pos.DiagonalRightDown()))
                    nrOfBreezes++;
        
        if(w.isValidPosition(p_Pos.rightAcross()))
            if(w.isVisited(p_Pos.rightAcross()))
                if(w.hasBreeze(p_Pos.rightAcross()))
                    nrOfBreezes++;
        
        return nrOfBreezes;
    }
    
    
       private int randInt(int min, int max) {

       // NOTE: Usually this should be a field rather than a method
       // variable so that it is not re-seeded every call.
       Random rand = new Random();
       
       // nextInt is normally exclusive of the top value,
       // so add 1 to make it inclusive
       int randomNum = rand.nextInt((max - min) + 1) + min;

       return randomNum;
   }
}



