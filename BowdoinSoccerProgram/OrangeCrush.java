import java.awt.*;

public class OrangeCrush extends Player {
    static final int CONTROL_TIME = 13;
    static final int WINGSPAN = 8;
    static final int WINGBACK = 4;
    static final int BALLDISTANCETOLEAD = 3;
    static final int SWEEPERDISTBACK = 8;
    static final int OPP_DIST_TO_PASS = 2;
    static final int Lead = 1;
    static final int NorthWing = 2;
    static final int SouthWing = 3;
    static final int Sweeper = 4;

    static int cycle;
    static int haveBall;
    static int leader;
    static int plx[];
    static int ply[];
    static int roles[];
    static int ball[];
    static int balld[];
    static int synchro[];

    public void InitializeGame () {
        cycle = -1;
        haveBall = 0;
        plx = new int[4];
        ply = new int[4];
        roles = new int[4];
        ball = new int[4];
        balld = new int[4];
        synchro = new int[4];
    }

    public void InitializePoint () {
        int i;
        leader = 0;
        haveBall = 0;
        for (i=0; i<=3; i++) {
            plx[i] = 0;
            ply[i] = 0;
            synchro[i] = 0;
        }
        roles[0] = Lead;
        roles[1] = NorthWing;
        roles[2] = SouthWing;
        roles[3] = Sweeper;
    }

    public int Player1() {
        int action = WEST;
        /* Mark where I am */
        plx[0] = GetLocation().x;
        ply[0] = GetLocation().y;
        ball[0] = HaveBall(0);
        balld[0] = GetBallDistance();
        /* Generate orders */
        Behave();
        for (int i = 0; i < 4; i++)
        synchro[i] = 1;
        switch (roles[0]) {
            case Lead: action =  Lead();
            break;
            case NorthWing: action =  NorthWing();
            break;
            case SouthWing: action =  SouthWing();
            break;
            case Sweeper: action =  Sweeper();
            break;
        }
        return action;
    }

    public int Player2() {
        /* Mark where I am */
        int action = WEST;
        plx[1] = GetLocation().x;
        ply[1] = GetLocation().y;
        ball[1] = HaveBall(1);
        balld[1] = GetBallDistance();
        synchro[1] = 0;
        switch (roles[1]) {
            case Lead: action =  Lead();
            break;
            case NorthWing: action =  NorthWing();
            break;
            case SouthWing: action =  SouthWing();
            break;
            case Sweeper: action =  Sweeper();
            break;
        }
        return action;
    }

    public int Player3() {
        int action = WEST;
        /* Mark where I am */
        plx[2] = GetLocation().x;
        ply[2] = GetLocation().y;
        ball[2] = HaveBall(2);
        balld[2] = GetBallDistance();
        synchro[2] = 0;
        switch (roles[2]) {
            case Lead: action =  Lead();
            break;
            case NorthWing: action =  NorthWing();
            break;
            case SouthWing: action =  SouthWing();
            break;
            case Sweeper: action =  Sweeper();
            break;
        }
        return action;
    }

    public int Player4() {
        int action = WEST;
        /* Mark where I am */
        plx[3] = GetLocation().x;
        ply[3] = GetLocation().y;
        ball[3] = HaveBall(3);
        balld[3] = GetBallDistance();
        synchro[3] = 0;
        switch (roles[3]) {
            case Lead: action =  Lead();
            break;
            case NorthWing: action =  NorthWing();
            break;
            case SouthWing: action =  SouthWing();
            break;
            case Sweeper: action =  Sweeper();
            break;
        }
        return action;
    }

    public void WonPoint () {};
    public void LostPoint () {};
    public void GameOver () {};

    public int HaveBall(int id) {
        int BallDir = GetBallDirection();
        if ((GetBallDistance() == 1) &&
        ((BallDir == EAST) || (BallDir == NORTHEAST)
        || (BallDir == SOUTHEAST) || (BallDir == NORTH)
        || (BallDir == SOUTH))) {
            haveBall = CONTROL_TIME;
            return 1;
        }
        return 0;
    }

    public void Behave () {

        int newl = 0;
        int i;
        cycle = (cycle + 1) % 4;
        if (cycle == 0) {
            haveBall--;
        }

        /* Whoever has the ball gets to be the leader */
        for ( i = 0; i < 4; i++) {
            if (balld[i] < balld[newl])
            newl = i;
            if (ball[i] == 1) {
                Regroup(i);
                i = 5;
            }
        }
        if (i == 4) /* No one was on the ball, pick the closest guy */
        Regroup(newl);
        // if (haveBall <= 0) {
        //     leader = 0;
        // }
    }

    public void Regroup (int newLead) {
        int i, score, good;
        good = 0;   /* Make Java happy */
        for (i=0; i<4; i++) {
            roles[i] = 0;
        }

        leader = newLead;
        roles[leader] = Lead;

        /* southernmost unassigned player is south wing */
        score = -1;
        for (i=0; i<4; i++) {
            if (roles[i] == 0) {
                if (ply[i] > score) {
                    score = ply[i];
                    good = i;
                }
            }
        }
        roles[good] = SouthWing;

        /* northernmost unassigned player is north wing */
        score = 10000;
        for (i=0; i<4; i++) {
            if (roles[i] == 0) {
                if (ply[i] < score) {
                    score = ply[i];
                    good = i;
                }
            }
        }
        roles[good] = NorthWing;

        /* easternmost unassigned player is Sweeper */
        score = -1;
        for (i=0; i<4; i++) {
            if (roles[i] == 0) {
                if (plx[i] > score) {
                    score = plx[i];
                    good = i;
                }
            }
        }
        roles[good] = Sweeper;
    }

    public int Lead () {
        int i, kickSouth;
        int x = GetLocation().x;
        int y = GetLocation().y;

        /* If lead is east of the ball and an opponent is around, get the ball
        out of here */
        if ((GetOpponentDistance(1) < OPP_DIST_TO_PASS) &&
        ((Look(SOUTHWEST) == BALL) || (Look(WEST) == BALL) || (Look(NORTHWEST) == BALL)))
        return KICK;

        /* Try to kick away from the bulk of the players */
        kickSouth = 0;
        for (i=0; i<4; i++) {
            if ((y < FieldY() - 4) && (ply[i] < y)) {
                kickSouth++;
            }
            if ((y > 5) && (ply[i] > y)) {
                kickSouth--;
            }
        }

        /* Kick south if we "should" kick south or if we are near defending goal */
        if (Look(SOUTHWEST) == BALL) {
            if ((x > 3 * FieldX() / 4) || (kickSouth > 0)) {
                return KICK;
            }
            /* else try to move to kick straight west */
            return SOUTH;
        }

        /* Similarly for north */
        if (Look(NORTH) == BALL) {
            if ((x > 3 * FieldX() / 4) || (kickSouth < 0)) {
                return KICK;
            }
            return NORTH;
        }

        if (Look(WEST) == BALL) {
            /* If there is a strong preference to kick the ball north or south,
            try to move to do so */
            if (kickSouth >= 3) {
                return NORTH;
            }
            if (kickSouth <= -3) {
                return SOUTH;
            }
            /* Otherwise kick toward the goal */
            return KICK;
        }

        if (Look(NORTH) == BALL) {
            /* If an opponent can kick toward my goal, try to kick the ball
            away */
            if (Look(WEST) == OPPONENT && Look(NORTHWEST) == OPPONENT) {
                return KICK;
            }

            /* If ball is near my defending goal, really try to kick it */
            if ((x > 3 * FieldX() / 4) &&
            (Look(WEST) == OPPONENT || Look(NORTHWEST) == OPPONENT)) {
                return KICK;
            }

            /* If there are no opponents and I want to kick south, move to try
            to do so */
            if ((kickSouth >= 0) && Look(NORTHEAST) == EMPTY) {
                return(NORTHEAST);
            }

            /* else if I want to kick north, just move E so I can kick NORTHWEST */
            return(EAST);
        }

        if (Look(NORTHEAST) == BALL) {
            /* If an opponent can get to the ball, get between it and the ball */
            if (Look(NORTH) == EMPTY && (Look(NORTHWEST) == OPPONENT)) {
                return(NORTH);
            }
            /* else get between the ball and the defending goal */
            return(EAST);
        }

        if (Look(EAST) == BALL) {
            /* Get into position to kick the ball, if possible */
            if ((kickSouth > 0) && Look(NORTHEAST) == EMPTY) {
                return(NORTHEAST);
            }
            if ((kickSouth < 0) && Look(SOUTHEAST) == EMPTY) {
                return(SOUTHEAST);
            }

            /* else try to block any opponents */
            if (Look(WEST) == OPPONENT) {
                return(WEST);
            }

            /* else just move out of the way */
            if (Look(NORTH) == EMPTY) {
                return(NORTH);
            }
            return(SOUTH);
        }

        if (Look(SOUTHEAST) == BALL) {
            /* If an opponent can get to the ball, get between it and the ball */
            if (Look(SOUTH) == EMPTY && (Look(SOUTHWEST) == OPPONENT)) {
                return(SOUTH);
            }
            /* else get between the ball and the defending goal */
            return(EAST);
        }

        if (Look(SOUTH) == BALL) {
            /* If an opponent can kick toward my goal, try to kick the ball
            away */
            if (Look(WEST) == OPPONENT && (Look(SOUTHWEST) == OPPONENT)) {
                return(KICK);
            }

            /* If ball is near my defending goal, really try to kick it */
            if ((x > 3 * FieldX() / 4) &&
            (Look(WEST) == OPPONENT || Look(SOUTHWEST) == OPPONENT)) {
                return(KICK);
            }

            /* If there are no opponents and I want to kick south, move to try
            to do so */
            if ((kickSouth >= 0) && Look(SOUTHEAST) == EMPTY) {
                return(SOUTHEAST);
            }

            /* else if I want to kick north, just move E so I can kick NORTHWEST */
            return(EAST);
        }

        /* else just move toward the ball */
        return(GetBallDirection());
    }

    public int NorthWing () {
        int x;
        int y;
        int ew = -1;
        int ns = -1;
        x = GetLocation().x;
        y = GetLocation().y;

        /* If near the ball, act like a leader */
        if (GetBallDistance() < BALLDISTANCETOLEAD) {
            return(Lead());
        }

        /* Try to get into position */
        if (Look(NORTH) == EMPTY && (y > ply[leader] - WINGSPAN)) {
            ns = NORTH;
        }
        if (Look(SOUTH) == EMPTY && (y < ply[leader] - WINGSPAN)) {
            ns = SOUTH;
        }
        if ((x < plx[leader]) && (y == ply[leader])) {
            ns = SOUTH;
        }
        if (Look(WEST) == EMPTY && (x > plx[leader] + WINGBACK)) {
            ew = WEST;
        }
        if (Look(EAST) == EMPTY && (x < plx[leader] + WINGBACK)) {
            ew = EAST;
        }

        if ((ew == EAST) && (ns == NORTH)) {
            return(NORTHEAST);
        }
        if ((ew == EAST) && (ns == SOUTH)) {
            return(SOUTHEAST);
        }
        if ((ew == WEST) && (ns == NORTH)) {
            return(NORTHWEST);
        }
        if ((ew == WEST) && (ns == SOUTH)) {
            return(SOUTHWEST);
        }
        if (ew == EAST) {
            return(EAST);
        }
        if (ew == WEST) {
            return(WEST);
        }
        if (ns == NORTH) {
            return(NORTH);
        }
        if (ns == SOUTH) {
            return(SOUTH);
        }
        return(GetBallDirection());
    }

    public int SouthWing () {
        int x;
        int y;
        int ew = -1;
        int ns = -1;
        x = GetLocation().x;
        y = GetLocation().y;

        /* If near the ball, act like a leader */
        if (GetBallDistance() < BALLDISTANCETOLEAD) {
            return(Lead());
        }

        /* Try to get into position */
        if (Look(NORTH) == EMPTY && (y > ply[leader] + WINGSPAN)) {
            ns = NORTH;
        }
        if (Look(SOUTH) == EMPTY && (y < ply[leader] + WINGSPAN)) {
            ns = SOUTH;
        }
        if ((x < plx[leader]) && (y == ply[leader])) {
            ns = NORTH;
        }
        if (Look(WEST) == EMPTY && (x > plx[leader] + WINGBACK)) {
            ew = WEST;
        }
        if (Look(EAST) == EMPTY && (x < plx[leader] + WINGBACK)) {
            ew = EAST;
        }

        if ((ew == EAST) && (ns == NORTH)) {
            return(NORTHEAST);
        }
        if ((ew == EAST) && (ns == SOUTH)) {
            return(SOUTHEAST);
        }
        if ((ew == WEST) && (ns == NORTH)) {
            return(NORTHWEST);
        }
        if ((ew == WEST) && (ns == SOUTH)) {
            return(SOUTHWEST);
        }
        if (ew == EAST) {
            return(EAST);
        }
        if (ew == WEST) {
            return(WEST);
        }
        if (ns == NORTH) {
            return(NORTH);
        }
        if (ns == SOUTH) {
            return(SOUTH);
        }
        return(GetBallDirection());
    }

    public int Sweeper () {
        int x;
        int y;
        int ew = -1;
        int ns = -1;
        x = GetLocation().x;
        y = GetLocation().y;

        /* If near the ball, act like a leader */
        if (GetBallDistance() < BALLDISTANCETOLEAD) {
            return(Lead());
        }

        /* Try to get into position */
        if (Look(NORTH) == EMPTY && (y > ply[leader])) {
            ns = NORTH;
        }
        if (Look(SOUTH) == EMPTY && (y < ply[leader])) {
            ns = SOUTH;
        }
        if ((x < plx[leader]) && (y == ply[leader])) {
            ns = SOUTH;
        }


        if (Look(WEST) == EMPTY && (x > plx[leader] + SWEEPERDISTBACK)) {
            ew = WEST;
        }
        if (Look(EAST) == EMPTY && (x < plx[leader] + SWEEPERDISTBACK)) {
            ew = EAST;
        }

        if ((ew == EAST) && (ns == NORTH)) {
            return(NORTHEAST);
        }
        if ((ew == EAST) && (ns == SOUTH)) {
            return(SOUTHEAST);
        }
        if ((ew == WEST) && (ns == NORTH)) {
            return(NORTHWEST);
        }
        if ((ew == WEST) && (ns == SOUTH)) {
            return(SOUTHWEST);
        }
        if (ew == EAST) {
            return(EAST);
        }
        if (ew == WEST) {
            return(WEST);
        }
        if (ns == NORTH) {
            return(NORTH);
        }
        if (ns == SOUTH) {
            return(SOUTH);
        }

        return(GetBallDirection());
    }

    public int getAction() {
        switch(ID)
        {
            case 1:
            return Player1();
            case 2:
            return Player2();
            case 3:
            return Player3();
            case 4:
            return Player4();
        }
        return BALL;
    }

}
