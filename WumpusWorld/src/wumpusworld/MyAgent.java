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
    private ArrayList<Position> m_PotentialWumpus;
    private ArrayList<Position> m_Pits; //Can include potetial pits to.
    private ArrayList<Position> m_MoveQueue;
    private Position m_WumpusPos;
    int deadend = 0;
    int turns = 0;
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
        m_PotentialWumpus = new ArrayList<>();
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
            if(!m_Breezes.contains(pos))
            {
                m_Breezes.add(pos);
                addPits(pos);
            }
            
            
            System.out.println("I am in a Breeze");
        }
        if (w.hasStench(pos))
        {
            if(!m_Stenches.contains(pos))
            {
                m_Stenches.add(pos);
                addWumpus(pos);
            }
            //If we have found more than one stench try to locate the wumpus 
            //and kill it
            if(m_Stenches.size() > 1)
            {
                int wumpusDir = locateWumpus(pos);
                if(wumpusDir > -1)
                {
                    if(wumpusDir == w.getDirection())
                    {
                        w.doAction(w.A_SHOOT);
                        if(!w.hasStench(pos))
                        {
                            m_PotentialWumpus.clear();
                            m_Stenches.clear();
                        }
                        return;
                    }
                    else
                    {
                        move(wumpusDir);
                        return;
                    }
                }
            }
            
           

            
            System.out.println("I am in a Stench");
        }
        if (w.hasPit(pos))
        {
            if(!m_Pits.contains(pos))
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
            if(pos.equals(m_MoveQueue.get(0)))
                m_MoveQueue.remove(0);
            
        if(explore(pos))
        {
            Position nPos = new Position(w.getPlayerX(), w.getPlayerY());
            
            
            for(int i = 0; i < m_Pits.size(); i++)
            {
                if(nPos.equals(m_Pits.get(i)))
                    if(!w.hasPit(nPos))
                        m_Pits.remove(i);
            }
            return;
        }
        
        
        
        //Random move actions
         System.out.println("Doing a random move!");
        
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
            int i = 0;
            //Loops breaks when a destination that has not been explored
            //is found, otherwise it runs a least 5000 times before it gives up
            //on finding a new destination.
            while(i < 5000)
            {
                Position dest = new Position(randInt(1, 4), randInt(1, 4));
                if(w.isUnknown(dest))
                {
                    m_MoveQueue.add(dest);
                    break;
                }
                i++;
            }
            
            if(!m_MoveQueue.isEmpty())
            {
                System.out.println("Getting a new destination. Heading to X:" + m_MoveQueue.get(0).X + " Y: " + m_MoveQueue.get(0).Y);
            }
            else return false;
            
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
        ArrayList<Integer> availableDir = new ArrayList<>();
        Position prevPos  = new Position(w.getPrevPlayerPositionX(), w.getPrevPlayerPositionY());
        if(p_PlayerPos.X > p_Destination.X)
            if(!checkIfHazard(p_PlayerPos.left()))
                if(!prevPos.equals(p_PlayerPos.left()))
                    availableDir.add(World.DIR_LEFT);
        
        if(p_PlayerPos.X < p_Destination.X)
            if(!checkIfHazard(p_PlayerPos.right()))
                if(!prevPos.equals(p_PlayerPos.right()))
                    availableDir.add(World.DIR_RIGHT);
        
        if(p_PlayerPos.Y < p_Destination.Y)
            if(!checkIfHazard(p_PlayerPos.up()))
                if(!prevPos.equals(p_PlayerPos.up()))
                    availableDir.add(World.DIR_UP);
        
        if(p_PlayerPos.Y > p_Destination.Y)
            if(!checkIfHazard(p_PlayerPos.down()))
                if(!prevPos.equals(p_PlayerPos.down()))
                    availableDir.add(World.DIR_DOWN);
        
        if(!availableDir.isEmpty())
        {
            if(availableDir.contains(w.getDirection()))
                return w.getDirection();
            else
                return availableDir.get(randInt(0, availableDir.size() - 1));
        }
        else
        {
            
            if(w.isVisited(p_PlayerPos.left()))
                availableDir.add(World.DIR_LEFT);
            if(w.isVisited(p_PlayerPos.right()))
                availableDir.add(World.DIR_RIGHT);
            if(w.isVisited(p_PlayerPos.up()))
                availableDir.add(World.DIR_UP);
            if(w.isVisited(p_PlayerPos.down()))
                availableDir.add(World.DIR_DOWN);
            

            if(!availableDir.isEmpty())
            {
                if(availableDir.contains(w.getDirection()))
                    return w.getDirection();
                else
                    return availableDir.get(randInt(0, availableDir.size() - 1));
            }
            else
            {
                System.out.println("Can't find a direction!");
                return World.DIR_UP;
            }
        }
        
        
        
    }
    
    private void addPits(Position p_Pos)
    {
        if(w.isUnknown(p_Pos.up()))
        {
            if(m_PotentialWumpus.contains(p_Pos.up()))
            {
                 m_PotentialWumpus.remove(p_Pos.up());
            }
            if(!m_Pits.contains(p_Pos.up()))
                m_Pits.add(p_Pos.up());
        }
        if(w.isUnknown(p_Pos.down()))
        {
            if(m_PotentialWumpus.contains(p_Pos.down()))
            {
                 m_PotentialWumpus.remove(p_Pos.down());
            }
            else if(!m_Pits.contains(p_Pos.down()))
                m_Pits.add(p_Pos.down());
        }
        if(w.isUnknown(p_Pos.left()))
        {
            if(m_PotentialWumpus.contains(p_Pos.left()))
            {
                 m_PotentialWumpus.remove(p_Pos.left());
            }
            else if(!m_Pits.contains(p_Pos.left()))
                m_Pits.add(p_Pos.left());
        }
        if(w.isUnknown(p_Pos.right()))
        {
            if(m_PotentialWumpus.contains(p_Pos.right()))
            {
                 m_PotentialWumpus.remove(p_Pos.right());
            }
            else if(!m_Pits.contains(p_Pos.right()))
                m_Pits.add(p_Pos.right());
        }
    }

    private void addWumpus(Position p_Pos)
    {
        if(w.isUnknown(p_Pos.up()))
        {
            if(m_Pits.contains(p_Pos.up()))
            {
                 m_Pits.remove(p_Pos.up());
            }
            else if(!m_PotentialWumpus.contains(p_Pos.up()))
                m_PotentialWumpus.add(p_Pos.up());
        }
        if(w.isUnknown(p_Pos.down()))
        {
            if(m_Pits.contains(p_Pos.down()))
            {
                 m_Pits.remove(p_Pos.down());
            }
            else if(!m_PotentialWumpus.contains(p_Pos.down()))
                m_PotentialWumpus.add(p_Pos.down());
        }
        if(w.isUnknown(p_Pos.left()))
        {
            if(m_Pits.contains(p_Pos.left()))
            {
                m_Pits.remove(p_Pos.left());
            }
            else if(!m_PotentialWumpus.contains(p_Pos.left()))
                m_PotentialWumpus.add(p_Pos.left());
            
        }
        if(w.isUnknown(p_Pos.right()))
        {
            if(m_Pits.contains(p_Pos.right()))
            {
                m_Pits.remove(p_Pos.right());
            }
            else if(!m_PotentialWumpus.contains(p_Pos.right()))
                m_PotentialWumpus.add(p_Pos.right());
        }
    }
    
    private boolean checkIfHazard(Position p_Pos)
    {
        if(m_Pits.contains(p_Pos))
               return true;
        if(m_PotentialWumpus.contains(p_Pos))
               return true;
                
        return false;
    }
    
    private int addUnknownPit(Position p_Pos)
    {
                int nrOfBreezes = 1;
        if(w.isVisited(p_Pos.DiagonalRightUp()))
            if(w.hasBreeze(p_Pos.DiagonalRightUp()))
                nrOfBreezes++;
        
        if(w.isVisited(p_Pos.DiagonalRightDown()))
            if(w.hasBreeze(p_Pos.DiagonalRightDown()))
                nrOfBreezes++;
        
        if(w.isVisited(p_Pos.rightAcross()))
            if(w.hasBreeze(p_Pos.rightAcross()))
                nrOfBreezes++;
        
        return nrOfBreezes;
    }
    
    private int locateWumpus(Position p_Pos)
    {
        for(Position p : m_PotentialWumpus)
        {
            if(p_Pos.equals(p.right()))
            {
                m_WumpusPos = p;
                return World.DIR_LEFT;
            }
            if(p_Pos.equals(p.left()))
            {
                m_WumpusPos = p;
                return World.DIR_RIGHT;
            }
            if(p_Pos.equals(p.up()))
            {
                m_WumpusPos = p;
                return World.DIR_DOWN;
            }
            if(p_Pos.equals(p.down()))
            {
                m_WumpusPos = p;
                return World.DIR_UP;
            }
        }
        return -1;
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



