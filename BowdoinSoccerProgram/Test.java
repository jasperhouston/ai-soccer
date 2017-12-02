import java.awt.*;
/**
 * Write a description of class Test here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Test
{
    static public final int NORTH = 0;
    static public final int NORTHEAST = 1;
    static public final int EAST = 2;
    static public final int SOUTHEAST = 3;
    static public final int SOUTH = 4;
    static public final int SOUTHWEST = 5;
    static public final int WEST = 6;
    static public final int NORTHWEST = 7;
    static public final int PLAYER = 8;
    static public final int BALL = 9;
    static public final int KICK = 10;
    static public final int BOUNDARY = 11;
    static public final int EASTPLAYER = 12;
    static public final int WESTPLAYER = 13;
    static public final int OPPONENT = 14;
    static public final int TEAMMATE = 15;
    static public final int EMPTY = 16;
    static public final int GOALLINE = 17;
    static public final int SIDELINE = 18;
    static public final int XSQUARES = 80;
    static public final int YSQUARES = 40;
    // instance variables - replace the example below with your own
    public static void main(String[] args) {
        Point ball = new Point(2,2);
        Point loc1 = new Point(0,1);
        System.out.println(GetDistance(ball, loc1));
        System.out.println(GetDirection(loc1, ball));
    }



    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */

    private static float GetDistance(Point A, Point B)
    {
        return (float)Math.sqrt((A.x - B.x)*(A.x - B.x) + (A.y-B.y)*(A.y-B.y));
    }
    
    /**  Returns the direction a player at origin would have to move in to get to Target.  Side is
    used to reverse things for the west team.  I've got to be making this more complicated
    than it needs to be. */
    private static int GetDirection(Point Origin, Point Target)
    {
        Point Delta = new Point(Origin.x -Target.x, Origin.y -Target.y);


        /*  We can divide things up into 4 quadrants by inspecting the signs of Delta.x and Delta.y
        + +  |   - +
        ------|------
        + -   |   - -

        This means that we can narrow the direction down to three choices right off the bat.
        The shortest path toward the ball will either be along the major axis or the diagonal
        through the quadrant.  This narrows our choice down to two directions.  We can use the
        tangent (watching out for division by zero) to determine if the ball is closer to the diagonal
        path or the straight one.  If .41421356 < abs(tan) < 2.41421356 then the best approximation
        is along the diagonal path.  (Note that the two messy decimals are inverses - this let's us
        simplify things a little more later on so we only need to do one test after our div by 0 check).
        Those two messy decimals are the tangents of 22.5 and 67.5 degrees, BTW.
        Otherwise it's along the major axis.  I wonder if this is any
        faster than the brute force approach...  I'd only be testing 8 cases...
         */

        if(Delta.x>=0)
        {
            if(Delta.y>=0)  // DX>=0, DY>=0
            {
                if(Math.abs(Delta.x)>=Math.abs(Delta.y))  //  We're X major
                {
                    if(Math.abs(((float)Delta.y)/((float)Delta.x))>.41421356237)
                        return NORTHWEST;
                    else
                        return WEST;
                }
                else  //  it's Y major
                {
                    //  Note that it's X/Y now instead of Y/X as above
                    //  Since the player can never be on the ball and we're always dividing by the
                    //  major axis, we'll never [in theory] divide by zero.
                    if(Math.abs(((float)Delta.x)/((float)Delta.y))>.41421356237)
                        return NORTHWEST;
                    else
                        return NORTH;
                }
            }
            else // DX>=0, DY<0
            {
                if(Math.abs(Delta.x)>=Math.abs(Delta.y))  //  We're X major
                {
                    if(Math.abs(((float)Delta.y)/((float)Delta.x))>.41421356237)
                        return SOUTHWEST;
                    else
                        return WEST;
                }
                else  //  it's Y major
                {
                    //  Note that it's X/Y now instead of Y/X as above
                    //  Since the player can never be on the ball and we're always dividing by the
                    //  major axis, we'll never [in theory] divide by zero.
                    if(Math.abs(((float)Delta.x)/((float)Delta.y))>.41421356237)
                        return SOUTHWEST;
                    else
                        return SOUTH;
                }
            }  //  end DX>=0, DY<0
        } //  end DX>=0
        else  //  Delta.x<0; equals case handled with greater than
        {
            if(Delta.y>=0)  // DX<0, DY>=0
            {
                if(Math.abs(Delta.x)>=Math.abs(Delta.y))  //  We're X major
                {
                    if(Math.abs(((float)Delta.y)/((float)Delta.x))>.41421356237)
                        return NORTHEAST;
                    else
                        return EAST;
                }
                else  //  it's Y major
                {
                    //  Note that it's X/Y now instead of Y/X as above
                    //  Since the player can never be on the ball and we're always dividing by the
                    //  major axis, we'll never [in theory] divide by zero.
                    if(Math.abs(((float)Delta.x)/((float)Delta.y))>.41421356237)
                        return NORTHEAST;
                    else
                        return NORTH;
                }
            }
            else // DX<0, DY<0
            {
                if(Math.abs(Delta.x)>=Math.abs(Delta.y))  //  We're X major
                {
                    if(Math.abs(((float)Delta.y)/((float)Delta.x))>.41421356237)
                        return SOUTHEAST;
                    else
                        return EAST;
                }
                else  //  it's Y major
                {
                    //  Note that it's X/Y now instead of Y/X as above
                    //  Since the player can never be on the ball and we're always dividing by the
                    //  major axis, we'll never [in theory] divide by zero.
                    if(Math.abs(((float)Delta.x)/((float)Delta.y))>.41421356237)
                        return SOUTHEAST;
                    else
                        return SOUTH;
                }
            }  //  end DX<0, DY<0
        }  //  end DX<0
    }  //  End GetBallDirection
}
