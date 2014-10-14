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
    public int m_X;
    public int m_Y;
    
    public Position(int p_X, int p_Y)
    {
        m_X = p_X;
        m_Y = p_Y;
    }
    
    public static boolean equals(Position p_Pos1, Position p_Pos2)
    {
        if(p_Pos1.m_X == p_Pos2.m_X)
            if(p_Pos1.m_Y == p_Pos2.m_Y)
                return true;
        
        return false;
    }
    
}
