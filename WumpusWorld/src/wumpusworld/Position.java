/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wumpusworld;

/**
 *
 * @author Christian
 */
public class Position {
    public int X;
    public int Y;
    
    public Position(int p_X, int p_Y)
    {
        X = p_X;
        Y = p_Y;
    }
    
    public static boolean equals(Position p_Pos1, Position p_Pos2)
    {
        if(p_Pos1.X == p_Pos2.X)
            if(p_Pos1.Y == p_Pos2.Y)
                return true;
        
        return false;
    }
    
    public Position up()
    {
        return new Position(X, Y + 1);
    }
    
    public Position down()
    {
        return new Position(X, Y - 1);
    }
        
    public Position right()
    {
        return new Position(X + 1, Y);
    }
            
    public Position left()
    {
        return new Position(X - 1, Y);
    }
    public Position DiagonalRightUp()
    {
        return new Position(X + 1, Y + 1);
    }
    public Position DiagonalRightDown()
    {
        return new Position(X + 1, Y - 1);
    }
    
    public Position DiagonalLeftUp()
    {
        return new Position(X - 1, Y + 1);
    }
    public Position DiagonalLeftDown()
    {
        return new Position(X - 1, Y - 1);
    }
    
    public Position rightAcross()
    {
        return new Position(X + 2, Y);
    }   
    
    public Position leftAcross()
    {
        return new Position(X - 2, Y);
    }
    
    public Position upAcross()
    {
        return new Position(X, Y + 2);
    }  
    
    public Position downAcross()
    {
        return new Position(X, Y - 2);
    }  
}
