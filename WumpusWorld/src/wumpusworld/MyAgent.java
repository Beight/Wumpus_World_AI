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
    private ArrayList<Position> m_PotentialPits;
    private ArrayList<Position> m_MoveQueue;
    private ArrayList<Position> m_PrevPos;
    private boolean first = true;
    private int m_notReachDest = 0;
    private int m_turns = 0;
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
        m_PotentialPits = new ArrayList<>();
        m_PotentialWumpus = new ArrayList<>();
        m_MoveQueue = new ArrayList<>();
        m_PrevPos = new ArrayList<>();
        w = world;
    }
    
    /**
     * Asks your solver agent to execute an action.
     */
    public void doAction()
    {
        //Location of the player
        Position pos = new Position(w.getPlayerX(), w.getPlayerY());
        m_PrevPos.add(new Position(w.getPrevPlayerPositionX(), w.getPrevPlayerPositionY()));
        
        updatePits();
        updateWumpus();
        
        
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
            //If we have found a new breeze save it to the list and 
            //add potential pits around it.
            if(!m_Breezes.contains(pos))
            {
                m_Breezes.add(pos);
                addPits(pos);
            }
            
            System.out.println("I am in a Breeze");
        }
        if (w.hasStench(pos))
        {
            //If we have found a new stench add it to the list
            //and add potential wumpus locations around it.
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
                m_Pits.add(pos);
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
        
        
        
        //If we have reached our destination or spent to many turns looking for it
        //remove it from the list so we can get a new one.
        if(!m_MoveQueue.isEmpty())
        {
            if(pos.equals(m_MoveQueue.get(0)))
            {
                m_turns = 0;
                m_MoveQueue.remove(0);
                m_PrevPos.clear();
                m_notReachDest = 0;
            }
            else if(m_turns > 6)
            {
                m_turns = 0;
                m_MoveQueue.remove(0);
                m_PrevPos.clear();
                m_notReachDest++;
            }
        }
        
        if(explore(pos))
            return;
        
        
        
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
                Position dest;
                if(first)
                {
                    dest = new Position(randInt(2, 4), randInt(2, 4));
                    first = false;
                }
                else
                    dest = new Position(randInt(1, 4), randInt(1, 4));
                
                
                if(w.isUnknown(dest) && !checkIfPotentialHazard(dest))
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
            else 
            {
                //If no safe destination are found set a potential pit as destination.
                if(!m_PotentialPits.isEmpty())
                    m_MoveQueue.add(m_PotentialPits.get(0));
            }
            
        }
        
        //Calculate which direction is needed to get to goal destination.
        int dir = -1;
        if(!m_MoveQueue.isEmpty())
            dir = calcDir(p_Pos, m_MoveQueue.get(0));
        
        //No direction was found.
        if(dir == -1)
            return false;
        
        //Move in the direction of the goal destination.
        if(move(dir))
            return true;
        
     
        //If this return statement is reached the move was invalid for some reason.
        //If this occurs something is wrong with the calcDir method or move method.
        return false;
    }
    
    private boolean move(int p_Dir)
    {
        if(w.getDirection() == p_Dir)
        {
            w.doAction(World.A_MOVE);
            m_turns++;
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
        Position prevPos = new Position(w.getPrevPlayerPositionX(), w.getPrevPlayerPositionY());
        if(p_PlayerPos.X > p_Destination.X)
            if(!checkIfPotentialHazard(p_PlayerPos.left()))
                if(!prevPos.equals(p_PlayerPos.left()))
                    availableDir.add(World.DIR_LEFT);
        
        if(p_PlayerPos.X < p_Destination.X)
            if(!checkIfPotentialHazard(p_PlayerPos.right()))
                if(!prevPos.equals(p_PlayerPos.right()))
                    availableDir.add(World.DIR_RIGHT);
        
        if(p_PlayerPos.Y < p_Destination.Y)
            if(!checkIfPotentialHazard(p_PlayerPos.up()))
                if(!prevPos.equals(p_PlayerPos.up()))
                    availableDir.add(World.DIR_UP);
        
        if(p_PlayerPos.Y > p_Destination.Y)
            if(!checkIfPotentialHazard(p_PlayerPos.down()))
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
            if(m_notReachDest > 1)
                return takeRisk(p_PlayerPos);
            
            if(w.isVisited(p_PlayerPos.left()) && !checkIfPotentialHazard(p_PlayerPos.left()))
                availableDir.add(World.DIR_LEFT);
            if(w.isVisited(p_PlayerPos.right()) && !checkIfPotentialHazard(p_PlayerPos.right()))
                availableDir.add(World.DIR_RIGHT);
            if(w.isVisited(p_PlayerPos.up()) && !checkIfPotentialHazard(p_PlayerPos.up()))
                availableDir.add(World.DIR_UP);
            if(w.isVisited(p_PlayerPos.down()) && !checkIfPotentialHazard(p_PlayerPos.down()))
                availableDir.add(World.DIR_DOWN);

            if(!availableDir.isEmpty())
            {
                if(availableDir.contains(w.getDirection()))
                    return w.getDirection();
                else
                    return availableDir.get(randInt(0, availableDir.size() - 1));
            }
            else
                return takeRisk(p_PlayerPos);
        }
    }   
        

    
    private int takeRisk(Position p_PlayerPos)
    {
        ArrayList<Integer> availableDir = new ArrayList<>();
        
        if(m_PotentialPits.contains(p_PlayerPos.left()))
            availableDir.add(World.DIR_LEFT);
        if(m_PotentialPits.contains(p_PlayerPos.right()))
            availableDir.add(World.DIR_RIGHT);
        if(m_PotentialPits.contains(p_PlayerPos.up()))
            availableDir.add(World.DIR_UP);
        if(m_PotentialPits.contains(p_PlayerPos.down()))
            availableDir.add(World.DIR_DOWN);

        if(!availableDir.isEmpty())
        {
            System.out.println("Can't find a safe direction, taking a risk!");
            if(availableDir.contains(w.getDirection()) && m_turns > 0)
            {
                m_notReachDest = 0;
                return w.getDirection();
            }
            else
                return availableDir.get(randInt(0, availableDir.size() - 1));
        }
        else
        {
            System.out.println("Can't find a direction!");
            return -1;
        } 
    }
    
    private void addPits(Position p_Pos)
    {
        if(w.isUnknown(p_Pos.up()) && !m_Pits.contains(p_Pos.up()))
            if(!m_PotentialPits.contains(p_Pos.up()))
                m_PotentialPits.add(p_Pos.up());
        
        if(w.isUnknown(p_Pos.down()) && !m_Pits.contains(p_Pos.down()))
            if(!m_PotentialPits.contains(p_Pos.down()))
                m_PotentialPits.add(p_Pos.down());
        
        if(w.isUnknown(p_Pos.left()) && !m_Pits.contains(p_Pos.left()))
            if(!m_PotentialPits.contains(p_Pos.left()))
                m_PotentialPits.add(p_Pos.left());

        if(w.isUnknown(p_Pos.right()) && !m_Pits.contains(p_Pos.right()))
            if(!m_PotentialPits.contains(p_Pos.right()))
                m_PotentialPits.add(p_Pos.right());
    }

    private void addWumpus(Position p_Pos)
    {
        if(w.isUnknown(p_Pos.up()) && !m_PotentialWumpus.contains(p_Pos.up()))
                m_PotentialWumpus.add(p_Pos.up());
        
        if(w.isUnknown(p_Pos.down()) && !m_PotentialWumpus.contains(p_Pos.down()))
            m_PotentialWumpus.add(p_Pos.down());
        
        if(w.isUnknown(p_Pos.left()) && !m_PotentialWumpus.contains(p_Pos.left()))
                m_PotentialWumpus.add(p_Pos.left());
        
        if(w.isUnknown(p_Pos.right()) && !m_PotentialWumpus.contains(p_Pos.right()))
                m_PotentialWumpus.add(p_Pos.right());
    }
    
    private boolean checkIfPotentialHazard(Position p_Pos)
    {
        if(!w.isValidPosition(p_Pos))
            return true;
            
        if(m_Pits.contains(p_Pos))
               return true;
        if(m_PotentialPits.contains(p_Pos))
               return true;
        if(m_PotentialWumpus.contains(p_Pos))
               return true;
                
        return false;
    }
    
    
    private int locateWumpus(Position p_Pos)
    {
        for(Position p : m_PotentialWumpus)
        {
            if(p_Pos.equals(p.right()))
            {
                m_PotentialPits.remove(p);
                return World.DIR_LEFT;
            }
            if(p_Pos.equals(p.left()))
            {
                m_PotentialPits.remove(p);
                return World.DIR_RIGHT;
            }
            if(p_Pos.equals(p.up()))
            {
                m_PotentialPits.remove(p);
                return World.DIR_DOWN;
            }
            if(p_Pos.equals(p.down()))
            {
                m_PotentialPits.remove(p);
                return World.DIR_UP;
            }
        }
        return -1;
    }
    
    private void updatePits()
    {
        ArrayList<Position> invalidPits = new ArrayList<>();
        
        //see if we have found any new info that invalidates a potential pit.
        for(Position p : m_PotentialPits)
        {
            if(w.isVisited(p.left()))
                if(!m_Breezes.contains(p.left()))
                {
                    invalidPits.add(p);
                    continue;
                }
            if(w.isVisited(p.right()))
                if(!m_Breezes.contains(p.right()))
                {
                    invalidPits.add(p);
                    continue;
                }
            if(w.isVisited(p.down()))
                if(!m_Breezes.contains(p.down()))
                {
                    invalidPits.add(p);
                    continue;
                }
            if(w.isVisited(p.up()))
                if(!m_Breezes.contains(p.up()))
                {
                    invalidPits.add(p);
                    continue;
                }
            
            if(w.isVisited(p))
                if(!w.hasPit(p))
                    invalidPits.add(p);
        }
        for(int i = 0; i < invalidPits.size(); i++)
        {
            m_PotentialPits.remove(invalidPits.get(i));
        }
        
        //Check if any of our potential pits is an actual pit
        for(Position p : m_PotentialPits)
        {
            int nrOfBreezesReq = 4;
            int nrOfBreezesFound = 0;
            
            if(!w.isValidPosition(p.left()))
                nrOfBreezesReq--;
            else if(m_Breezes.contains(p.left()))
                nrOfBreezesFound++;
                
            if(!w.isValidPosition(p.right()))
                nrOfBreezesReq--;
            else if(m_Breezes.contains(p.right()))
                nrOfBreezesFound++;
            
            if(!w.isValidPosition(p.down()))
                nrOfBreezesReq--;
            else if(m_Breezes.contains(p.down()))
                nrOfBreezesFound++;
            
            if(!w.isValidPosition(p.up()))
                nrOfBreezesReq--;
            else if(m_Breezes.contains(p.up()))
                nrOfBreezesFound++;
            
            if(nrOfBreezesReq == nrOfBreezesFound)
            {
                m_Pits.add(p); 
            }
        }

        for(int i = 0; i < m_Pits.size(); i++)
        {
            m_PotentialPits.remove(m_Pits.get(i));
        }
    }
    
    private void updateWumpus()
    {
        ArrayList<Position> invalidWumpus = new ArrayList<>();
        
        for(Position p : m_PotentialWumpus)
        {
            if(w.isVisited(p.left()))
                if(!w.hasStench(p.left()))
                {
                    invalidWumpus.add(p);
                    continue;
                }
            if(w.isVisited(p.right()))
                if(!w.hasStench(p.right()))
                {
                    invalidWumpus.add(p);
                    continue;
                }
            if(w.isVisited(p.down()))
                if(!w.hasStench(p.down()))
                {
                    invalidWumpus.add(p);
                    continue;
                }
            if(w.isVisited(p.up()))
                if(!w.hasStench(p.up()))
                {
                    invalidWumpus.add(p);
                    continue;
                }
            if(w.isVisited(p))
                if(!w.hasWumpus(p))
                    invalidWumpus.add(p);
        }
        for(int i = 0; i < invalidWumpus.size(); i++)
        {
            m_PotentialWumpus.remove(invalidWumpus.get(i));
        }
    }
    
    
    private int randInt(int min, int max) 
    {

       // NOTE: Usually this should be a field rather than a method
       // variable so that it is not re-seeded every call.
       Random rand = new Random();
       
       // nextInt is normally exclusive of the top value,
       // so add 1 to make it inclusive
       int randomNum = rand.nextInt((max - min) + 1) + min;

       return randomNum;
   }
}



