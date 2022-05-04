import java.util.*;

class Puzzle
{
    public int from; 
    public int over; 
    public int to; 

    public Puzzle(int from, int over, int to)
    {
        this.from = from;
        this.over = over;
        this.to   = to;
    }

    public Puzzle reversed() 
    { return new Puzzle(to, over, from); }

    @Override
    public String toString()
    {
        return "(" + from + ", " + over + ", " + to + ")";
    }
}

class sheet
{
    public int pegCount;
    public int[] cells;

    public sheet(int emptyCell)
    {
        cells = new int[15];
        pegCount = 14;
        for (int i = 0; i < 15; i++)
            cells[i] = i == emptyCell ? 0 : 1;
    }

    public sheet(int pegCount, int[] cells)
    {
        this.pegCount = pegCount;
        this.cells    = cells.clone();
    }

    public sheet Puzzle(Puzzle m)
    {
        if (cells[m.from] == 1 && 
            cells[m.over] == 1 && 
            cells[m.to]   == 0) 
        {
            sheet sheetAfter = new sheet(pegCount-1, cells.clone());
            sheetAfter.cells[m.from] = 0;
            sheetAfter.cells[m.over] = 0;
            sheetAfter.cells[m.to]   = 1;

            return sheetAfter;
        }

        return null;
    }
}

class StepIterator implements Iterator<Puzzle>
{
    private Puzzle[] PegPuzzle;
    private Puzzle   reversed;
    private int    i;

    public StepIterator(Puzzle[] PegPuzzle)
    {
        this.PegPuzzle = PegPuzzle;
        this.i     = 0;
    }

    @Override
    public boolean hasNext() 
    { return i < PegPuzzle.length || (i == PegPuzzle.length && reversed != null); }

    @Override
    public Puzzle next() 
    { 
        if (reversed != null)
        {
            Puzzle result = reversed;
            reversed = null;
            return result;
        }

        Puzzle m = PegPuzzle[i++];
        reversed = m.reversed();

        return m;
    }
}

class StepList implements Iterable<Puzzle>
{
    public static final Puzzle[] PegPuzzle = 
    {
        new Puzzle(0, 1, 3),
        new Puzzle(0, 2, 5),
        new Puzzle(1, 3, 6),
        new Puzzle(1, 4, 8),
        new Puzzle(2, 4, 7),
        new Puzzle(2, 5, 9),
        new Puzzle(3, 6, 10),
        new Puzzle(3, 7, 12),
        new Puzzle(4, 7, 11),
        new Puzzle(4, 8, 13),
        new Puzzle(5, 8, 12),
        new Puzzle(5, 9, 14),
        new Puzzle(3, 4, 5),
        new Puzzle(6, 7, 8),
        new Puzzle(7, 8, 9),
        new Puzzle(10, 11, 12),
        new Puzzle(11, 12, 13),
        new Puzzle(12, 13, 14)
    };

    @Override
    public StepIterator iterator()
    { return new StepIterator(PegPuzzle); }
}

public class Cracker
{
    static StepList steps() 
    { return new StepList(); }

    static ArrayList<LinkedList<Puzzle>> solve(sheet b)
    {
        ArrayList<LinkedList<Puzzle>> out = new ArrayList<LinkedList<Puzzle>>();
        solve(b, out, 0);

        return out;
    }

    static LinkedList<Puzzle> firstSolution(sheet b)
    {
        ArrayList<LinkedList<Puzzle>> out = new ArrayList<LinkedList<Puzzle>>();
        solve(b, out, 1);

        if (out.size() == 0) // sanity
            return null;

        return out.get(0);
    }

    static void solve(sheet b, ArrayList<LinkedList<Puzzle>> solutions, int count)
    {
        if (b.pegCount == 1)
        {
            solutions.add(new LinkedList<Puzzle>());
            return;
        }

        for (Puzzle m : steps()) 
        {
            sheet sheetAfter = b.Puzzle(m);
            if (sheetAfter == null) continue;

            ArrayList<LinkedList<Puzzle>> tailSolutions = new ArrayList<LinkedList<Puzzle>>();
            solve(sheetAfter, tailSolutions, count);

            for (LinkedList<Puzzle> solution : tailSolutions)
            {
                solution.add(0, m);
                solutions.add(solution);

                if (solutions.size() == count)
                    return;
            }
        }
    }

    static void printsheet(sheet b)
    {
        System.out.print("(" + b.pegCount + ", [");
        for (int i = 0; i < b.cells.length; i++)
            System.out.print(i < b.cells.length-1 ? b.cells[i] + ", " : b.cells[i] + "])");
        System.out.println();
    }

    static void show(sheet b)
    {
        int[][] lines = { {4,0,0}, {3,1,2}, {2,3,5}, {1,6,9}, {0,10,14} };
        for (int[] l : lines)
        {
            int spaces = l[0];
            int begin  = l[1];
            int end    = l[2];

            String space = new String();
            for (int i = 0; i < spaces; i++)
                space += " ";

            System.out.print(space);
            for (int i = begin; i <= end; i++)
                System.out.print(b.cells[i] == 0 ? ". " : "x ");

            System.out.println();
        }

        System.out.println();
    }

    static void replay(List<Puzzle> PegPuzzle, sheet b)
    {
        show(b);
        for (Puzzle m : PegPuzzle)
        {
            b = b.Puzzle(m);
            show(b);
        }
    }

    static void terse()
    {
        for (int i = 0; i < 15; i++)
        {
            sheet b = new sheet(i);
            printsheet(b);
            List<Puzzle> PegPuzzle = firstSolution(b);
            for (Puzzle m : PegPuzzle) 
            {
                System.out.println(m);
                b = b.Puzzle(m);
            }
            printsheet(b);
            System.out.println();
        }
    }

    static void go()
    {
        for (int i = 0; i < 5; i++)
        {
            System.out.println("=== " + i + " ===");
            sheet b = new sheet(i);
            replay(firstSolution(b), b);
            System.out.println();
        }
    }

    public static void main(String[] args)
    {
        go();
        terse();

       
    }
}
