package wumpusworld;

import java.util.Vector;

/**
 * This class handles an instance of the Wumpus World. It contains the world
 * state, which actions are available, and updates the world when an action
 * has been executed.
 * 
 * @author Johan Hagelbäck
 */
public class World 
{
    private int size;
    private String[][] w;
    private int pX = 1;
    private int pY = 1;
    private int prev_pX = 1;
    private int prev_pY = 1;
    private boolean wumpusAlive = true;
    private boolean hasArrow = true;
    private boolean isInPit = false;
    private boolean hasGold = false;
    private boolean gameOver = false;   
    private int score = 0;
    
    //Player Directions constants.
    public static final int DIR_UP = 0;
    public static final int DIR_RIGHT = 1;
    public static final int DIR_DOWN = 2;
    public static final int DIR_LEFT = 3;
    
    //Start direction
    private int dir = DIR_RIGHT;
    
    //Percepts constants.
    public static final String BREEZE = "B";
    public static final String STENCH = "S";
    public static final String PIT = "P";
    public static final String WUMPUS = "W";
    public static final String GLITTER = "G";
    public static final String GOLD = "T";
    public static final String UNKNOWN = "U";
    
    //Actions constants.
    public static final String A_MOVE = "m";
    public static final String A_GRAB = "g";
    public static final String A_CLIMB = "c";
    public static final String A_SHOOT = "s";
    public static final String A_TURN_LEFT = "l";
    public static final String A_TURN_RIGHT = "r";
    
    /**
     * Creates a new Wumpus World. The Wumpus World works with
     * any size 4 or larger, but only size 4 is supported by
     * the GUI.
     * 
     * @param size Size of the world.
     */
    public World(int size)
    {
        this.size = size;
        w = new String[size+1][size+1];
        
        for (int x = 0; x <= size; x++)
        {
            for (int y = 0; y <= size; y++)
            {
                w[x][y] = UNKNOWN;
            }
        }
        
        setVisited(1, 1);
    }
    
    /**
     * Returns the current score.
     * 
     * @return The score.
     */
    public int getScore()
    {
        return score;
    }
    
    /**
     * Returns the size of this Wumpus World.
     * 
     * @return The size 
     */
    public int getSize()
    {
        return size;
    }
    
    /**
     * Checks if the game has ended or not.
     * 
     * @return True if game is over, false if not.
     */
    public boolean gameOver()
    {
        return gameOver;
    }
    
    /**
     * Returns player X position.
     * 
     * @return X position.
     */
    public int getPlayerX()
    {
        return pX;
    }
    
    /**
     * Returns player Y position.
     * 
     * @return Y position.
     */
    public int getPlayerY()
    {
        return pY;
    } 
           
    /**
     * Checks if the player is in a pit and needs to
     * climb up.
     * 
     * @return True if in a pit, false otherwise.
     */
    public boolean isInPit()
    {
        return isInPit;
    }
    
    /**
     * Checks if the player has the arrow left.
     * 
     * @return True if player has the arrow, false otherwise.
     */
    public boolean hasArrow()
    {
        return hasArrow;
    }
    
    /**
     * Checks if the Wumpus is alive.
     * 
     * @return True if Wumpus is alive, false otherwise.
     */
    public boolean wumpusAlive()
    {
        return wumpusAlive;
    }
    
    /**
     * Checks if the player carries the gold treasure.
     * 
     * @return True if player has the gold, false otherwise.
     */
    public boolean hasGold()
    {
        return hasGold;
    }
    
    /**
     * Returns the current direction of the player.
     * 
     * @return Direction (see direction constants)
     */
    public int getDirection()
    {
        return dir;
    }
    
    /**
     * Checks if a square has a breeze. Returns false
     * if the position is invalid, or if the square is
     * unknown.
     * 
     * @param x X position
     * @param y Y position
     * @return True if the square has a breeze
     */
    public boolean hasBreeze(Position p_Pos)
    {
        if (!isValidPosition(p_Pos)) return false;
        if (isUnknown(p_Pos)) return false;
        
        if (w[p_Pos.X][p_Pos.Y].contains(BREEZE))
            return true;
        else
            return false;
    }
    
    /**
     * Checks if a square has a stench. Returns false
     * if the position is invalid, or if the square is
     * unknown.
     * 
     * @param p_Pos position
     * @return True if the square has a stench
     */
    public boolean hasStench(Position p_Pos)
    {
        if (!isValidPosition(p_Pos)) return false;
        if (isUnknown(p_Pos)) return false;
        
        if (w[p_Pos.X][p_Pos.Y].contains(STENCH))
            return true;
        else
            return false;
    }
    
    /**
     * Checks if a square has glitter. Returns false
     * if the position is invalid, or if the square is
     * unknown.
     * 
     * @param p_Pos X position
     * @return True if the square has glitter
     */
    public boolean hasGlitter(Position p_Pos)
    {
        if (!isValidPosition(p_Pos)) return false;
        if (isUnknown(p_Pos)) return false;
        
        if (w[p_Pos.X][p_Pos.Y].contains(GLITTER))
            return true;
        else
            return false;
    }
    
    /**
     * Checks if a square has a pit. Returns false
     * if the position is invalid, or if the square is
     * unknown.
     * 
     * @param p_Pos position
     * @return True if the square has a pit
     */
    public boolean hasPit(Position p_Pos)
    {
        if (!isValidPosition(p_Pos)) return false;
        if (isUnknown(p_Pos)) return false;
        
        if (w[p_Pos.X][p_Pos.Y].contains(PIT))
            return true;
        else
            return false;
    }
    
    /**
     * Checks if the Wumpus is in a square. Returns false
     * if the position is invalid, or if the square is
     * unknown.
     * 
     * @param p_Pos position
     * @return True if the Wumpus is in the square
     */
    public boolean hasWumpus(Position p_Pos)
    {
        if (!isValidPosition(p_Pos)) return false;
        if (isUnknown(p_Pos)) return false;
        
        if (w[p_Pos.X][p_Pos.Y].contains(WUMPUS))
            return true;
        else
            return false;
    }
    
     /**
     * Checks if the player is in a square.
     * 
     * @param p_Pos position
     * @return True if the player is in the square
     */
    public boolean hasPlayer(Position p_Pos)
    {
        if (pX == p_Pos.X && pY == p_Pos.Y)
        {
            return true;
        }
        return false;
    }
    
     /**
     * Checks if a square is visited. Returns false
     * if the position is invalid.
     * 
     * @param p_Pos position
     * @return True if the square is visited
     */
    public boolean isVisited(Position p_Pos)
    {
        if (!isValidPosition(p_Pos)) return false;
        
        return !isUnknown(p_Pos);
    }
    
    /**
     * Checks if a square is unknown. Returns false
     * if the position is invalid.
     * 
     * @param p_Pos position
     * @return True if the square is unknown
     */
    public boolean isUnknown(Position p_Pos)
    {
        if (!isValidPosition(p_Pos)) return false;
        
        if (w[p_Pos.X][p_Pos.Y].contains(UNKNOWN))
            return true;
        else
            return false;  
    }
    
    /**
     * Checks if a square is valid, i.e. inside
     * the bounds of the game world.
     * 
     * @param p_Pos position
     * @return True if the square is valid
     */
    public boolean isValidPosition(Position p_Pos)
    {
        if (p_Pos.X < 1) return false;
        if (p_Pos.Y < 1) return false;
        if (p_Pos.X > size) return false;
        if (p_Pos.Y > size) return false;
        return true;
    }
    
    /**
     * Adds a percept to a square.
     * 
     * @param x X position
     * @param y Y position
     * @param s Percept to add (see Percept constants)
     */
    private void append(int x, int y, String s)
    {
        if (!isValidPosition(new Position(x,y)))
            return;
        
        if (!w[x][y].contains(s))
        {
            w[x][y] += s;
        }
    }
    
    /**
     * Adds the Wumpus to a square.
     * 
     * @param x X position
     * @param y Y position
     */
    public void addWumpus(int x, int y)
    {
        if (!w[x][y].contains(WUMPUS))
        {
            append(x,y,WUMPUS);
            append(x-1,y,STENCH);
            append(x+1,y,STENCH);
            append(x,y-1,STENCH);
            append(x,y+1,STENCH);
        }
    }
    
    /**
     * Adds a pit to a square.
     * 
     * @param x X position
     * @param y Y position
     */
    public void addPit(int x, int y)
    {
        if (!w[x][y].contains(PIT))
        {
            append(x,y,PIT);
            append(x-1,y,BREEZE);
            append(x+1,y,BREEZE);
            append(x,y-1,BREEZE);
            append(x,y+1,BREEZE);
        }
    }
    
    /**
     * Adds the gold treasure to a square.
     * 
     * @param x X position
     * @param y Y position 
     */
    public void addGold(int x, int y)
    {
        if (!w[x][y].contains(GLITTER))
        {
            append(x,y,GLITTER);
        }
    }
    
    /**
     * Sets that a square has been visited.
     * 
     * @param x X position
     * @param y Y position 
     */
    private void setVisited(int x, int y)
    {
        if (w[x][y].contains(UNKNOWN))
        {
            w[x][y] = w[x][y].replaceAll(UNKNOWN, "");
        }
    }
    
    /**
     * Executes an action in the Wumpus World.
     * 
     * @param a Action string (see Action constants)
     * @return True if the action was successful, false if action failed.
     */
    public boolean doAction(String a)
    {
        if (gameOver) return false;
        
        //Each action costs 1 score
        score -= 1;
        
        if (a.equals(A_MOVE))
        {
            if (!isInPit)
            {
                if (dir == DIR_LEFT) return move(pX-1,pY);
                if (dir == DIR_RIGHT) return move(pX+1,pY);
                if (dir == DIR_UP) return move(pX,pY+1);
                if (dir == DIR_DOWN) return move(pX,pY-1);
            }
        }
        if (a.equals(A_TURN_LEFT))
        {
            dir--;
            if (dir < 0) dir = 3;
            return true;
        }
        if (a.equals(A_TURN_RIGHT))
        {
            dir++;
            if (dir > 3) dir = 0;
            return true;
        }
        if (a.equals(A_GRAB))
        {
            if (hasGlitter(new Position(pX,pY)))
            {
                w[pX][pY] = w[pX][pY].replaceAll(GLITTER, "");
                score += 1000;
                hasGold = true;
                gameOver = true;
                return true;
            }
        }
        if (a.equals(A_SHOOT))
        {
            if (hasArrow)
            {
                score -= 10;
                hasArrow = false;
                shoot();
                return true;
            }
        }
        if (a.equals(A_CLIMB))
        {
            isInPit = false;
        }
        
        //Action failed
        return false;
    }
    
    /**
     * Checks if the Wumpus has been hit by the arrow.
     */
    private void shoot()
    {
        if (dir == DIR_RIGHT)
        {
            for (int x = pX; x <= size; x++)
            {
                if (w[x][pY].contains(WUMPUS)) removeWumpus();
            }
        }
        if (dir == DIR_LEFT)
        {
            for (int x = pX; x >= 0; x--)
            {
                if (w[x][pY].contains(WUMPUS)) removeWumpus();
            }
        }
        if (dir == DIR_UP)
        {
            for (int y = pY; y <= size; y++)
            {
                if (w[pX][y].contains(WUMPUS)) removeWumpus();
            }
        }
        if (dir == DIR_DOWN)
        {
            for (int y = pY; y >= 0; y--)
            {
                if (w[pX][y].contains(WUMPUS)) removeWumpus();
            }
        }
    }
    
    /**
     * Removes the Wumpus (and Stench) from the Wumpus World.
     * Used when the Wumpus has been hit by the arrow.
     */
    private void removeWumpus()
    {
        for (int x = 1; x <= 4; x++)
        {
            for (int y = 1; y <= 4; y++)
            {
                w[x][y] = w[x][y].replaceAll(WUMPUS, "");
                w[x][y] = w[x][y].replaceAll(STENCH, "");
            }
        }
        
        wumpusAlive = false;
    }
    
    /**
     * Executes a move forward to a new square.
     * 
     * @param nX New X position
     * @param nY New Y position
     * @return True if the move actions was successful, false otherwise
     */
    private boolean move(int nX, int nY)
    {
        //Check if valid
        Position p = new Position(nX, nY);
        if (!isValidPosition(p))
        {
            return false;
        }
        prev_pX = pX;
        prev_pY = pY;
        pX = nX;
        pY = nY;
        
        setVisited(pX, pY);
        
        if(hasWumpus(p))
        {
            score -= 1000;
            gameOver = true;
        }
        if (hasPit(p))
        {
            score -= 1000;
            isInPit = true;
        }
        
        return true;    
    }
    
    public int getPrevPlayerPositionX()
    {
        return prev_pX;
    }
    public int getPrevPlayerPositionY()
    {
        return prev_pY;
    }
}
