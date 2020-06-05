import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;

public class NQueen {
    public static void main(String[] args) {
        Scanner in = new Scanner((System.in));
        System.out.print("Enter board size (for example 8) : ");
        int n = in.nextInt(), solution_number = n*10, crossover_number= n*4;//2*n children
        Pair[] fits = new Pair[solution_number];

        int[][][] population = initialize(solution_number,n);
        boolean answerNotFound = true;

        /*
        for (int i = 0; i < solution_number; i++) {
            printSolution(population[i]);
            System.out.println(fitness(population[i]));
            System.out.println();
            System.out.println();
        }*/

        long starttime = System.currentTimeMillis();

        //fitness calculation
        for (int i = 0; i <solution_number; i++) {
            fits[i] = new Pair(i,fitness(population[i]));
            if(fits[i].value==0){
                answerNotFound = false;
                System.out.println("ANSWER FOUND in first initializing\n");
                printSolution(population[i]);
                break;
            }
        }


        // find best solution #crossover_number
        // create children
        // mutation with some probability on all of the population
        // Fitness Calculation
        // replace children with worst solutions in population

        // we loop in this while loop till we find an answer
        // if we find it under 60 times looping while then it is finished
        // if not we reinitializing our population an start the process again
        int Gen = 0,limitGen = 60,iteration=1;
        while(answerNotFound)
        {
            // Parent Selection : find bests in population (#crossover_number)
            Pair [] bests = bestInPopulation(fits, crossover_number);


            // Crossover : produce off-springs
            int[][][] children = new int[crossover_number/2][][];
            int j = 0;
            for (int i = 0; i < bests.length /* crossover_number */; i++) {
                children[j] =
                        crossover(population[bests[i].index],population[bests[++i].index],
                                new Random().nextInt(n-1)+2);
                j++;
            }

            // Mutation on off-springs
            for (int i = 0; i < population.length; i++) {
                if(new Random().nextInt(10)<2)
                    mutation(population[i]);
            }


            // we must choose the best solution_number solutions from current population and children
            // Fitness Calculation for current population
            for (int i = 0; i <solution_number; i++) {
                fits[i].value = fitness(population[i]);
                fits[i].index = i;
                //System.out.print(fits[i].value+" ");
                if(fits[i].value==0)
                {
                    answerNotFound = false;
                    System.out.println();
                    System.out.printf("ANSWER FOUND! in generation %d of %d iteration\n",Gen+1,iteration);
                    printSolution(population[i]);
                    break;
                }
            }

            // finding worsts in current population and replace them with children
            Pair [] worsts = worstInPopulation(fits,crossover_number/2);

            //replace worsts with children
            for (int i = 0; i < children.length; i++) {
                population[worsts[i].index] = children[i];
            }

            Gen++;
            if(limitGen==Gen){
                population = initialize(solution_number,n);
                iteration++;
                for (int i = 0; i <solution_number; i++) {
                    fits[i] = new Pair(i,fitness(population[i]));
                    if(fits[i].value==0){
                        answerNotFound = false;
                        System.out.printf("ANSWER FOUND! in Reinitializing for %d iteration\n",iteration);
                        printSolution(population[i]);
                        break;
                    }
                }
                Gen=0;
            }
        }
        System.out.println("Time in milliseconds : ");
        System.out.println(System.currentTimeMillis()-starttime);

        String str = in.next();//type anything to close the window
    }

    // return i'th worst(max) index and fitness in population
    public static Pair[] worstInPopulation(Pair[] fits, int worstcount) {
        Pair[] f = new Pair[fits.length];
        Pair[] ret = new Pair[worstcount];
        int jj=0;
        for (int j = 0; j < f.length; j++)
            f[j] = new Pair(fits[j].index,fits[j].value);
        compare(f);
        for (int j = fits.length-worstcount; j < fits.length; j++) {
            ret[jj] = new Pair(f[j].index, f[j].value);
            jj++;
        }
        return ret;
    }

    // return i'th bests(min) index and fitness in population
    public static Pair[] bestInPopulation(Pair[] fits, int crossover_number) {
        Pair[] f = new Pair[fits.length];
        Pair[] ret = new Pair[crossover_number];

        for (int j = 0; j < f.length; j++)
            f[j] = new Pair(fits[j].index,fits[j].value);
        compare(f);
        for (int j = 0; j < ret.length; j++)
            ret[j] = new Pair(f[j].index,f[j].value);

        return ret;
    }

    // changes the rows of 2 queens in row n and m with eachother
    public static void mutation(int [][] sol){
        //change the rows of queens n and m in their columns.
        int n = new Random().nextInt(sol.length);
        int m = new Random().nextInt(sol.length);
        while(n==m)
            m = new Random().nextInt(sol.length);
        int xn = 0,yn = 0,xm = 0,ym = 0, c = 0;
        for (int j = 0; j < sol.length; j++) {
            for (int i = 0; i < sol.length; i++) {
                if(sol[i][j]==1)
                    c++;
                if(c==n) {
                    xn = i;
                    yn = j;
                }
                if(c==m) {
                    xm = i;
                    ym = j;
                }
            }
        }
        // changing n'th queens row to the m's row in the same column
        sol[xn][yn] = 0;
        sol[xm][yn] = 1;
        // changing m'th queens row to the n's row in the same column
        sol[xm][ym] = 0;
        sol[xn][ym] = 1;
    }

    // child = 0..k columns from sol1 and k..n columns from sol2
    public static int[][] crossover(int [][]sol1, int [][]sol2,int k){
        int [][] ret = new int[sol1.length][sol1.length];
        for (int j = 0; j < k; j++) {
            for (int i = 0; i < sol1.length; i++) {
                ret[i][j] = sol1[i][j];
            }
        }
        for (int j = k; j < sol2.length; j++) {
            for (int i = 0; i < sol2.length; i++) {
                ret[i][j] = sol2[i][j];
            }
        }
        return ret;
    }

    // count of wrong pairs
    public static int fitness(int [][]solution){
        int wrongQPairs = 0;
        for (int i = 0; i < solution.length; i++) {
            for (int j = 0; j < solution[0].length; j++) {
                //column checking
                for (int k = 0; k < solution.length; k++) {
                    if(solution[i][j]==1 && solution[k][j]==1 && k!=i){
                        wrongQPairs+=1;
                    }
                }
                //row checking
                for (int k = 0; k < solution.length; k++) {
                    if(solution[i][j]==1 && solution[i][k]==1 && k!=j){
                        wrongQPairs+=1;
                    }
                }
                // main diagonal
                int ii =  i>j ? i-j : 0;
                int jj =  j>=i ? j-i : 0;
                while (ii<solution.length && jj<solution[0].length){
                    if(solution[i][j]==1 && solution[ii][jj]==1 && ii!=i && jj!=j) {
                        wrongQPairs += 1;
                    }
                    ii++;
                    jj++;
                }

                //antidiagonal
                ii =  i+j < solution.length ? 0 : i+j-solution.length+1;
                jj =  i+j>= solution.length ? solution.length-1 : i+j;
                while (ii<solution.length && jj>= 0){
                    if(solution[i][j]==1 && solution[ii][jj]==1 && ii!=i && jj!=j) {
                        wrongQPairs += 1;
                    }
                    ii++;
                    jj--;
                }
            }
        }
        return wrongQPairs/2;
    }

    public static int[][][] initialize(int solution_number, int boardSize) {
        int[][][] ret = new int[solution_number][boardSize][boardSize];
        int row,preRow;
        for (int k = 0; k < solution_number; k++) {
            preRow = new Random().nextInt(boardSize);
            ret[k][preRow][0] = 1;
            for (int j = 1; j < boardSize; j++) {
                do {
                    row = new Random().nextInt(boardSize);
                } while (Math.abs(preRow-row)<2);
                // queen sutun j va j+1 bishtar mosavi 2 satr fasele dashte bashand
                //   0 1
                // 0
                // 1   *
                // 2 * *     baraye az beyn bordane chenin halat hayi ( agar sutun 0 satr 2 shod dar sutun 1
                // 3   *     satr haye 1 ,2 ,3 queen nagozarim.
                // 4
                // in kar baes mishavad initialize kardane population ta hududi agahane bashad.
                ret[k][row][j] = 1;
                preRow = row;
            }
        }
        return ret;
    }

    // sort by value
    public static void compare(Pair[] arr){
        Arrays.sort(arr, new Comparator<Pair>() {
            @Override
            public int compare(Pair o1, Pair o2) {
                return o1.value - o2.value;
            }
        });
    }

    public static void printSolution(int[][] sol){
        for (int ii = 0; ii < sol.length; ii++) {
            for (int jj = 0; jj < sol.length; jj++)
                System.out.print(sol[ii][jj] + " ");
            System.out.println();
        }
    }
}
class Pair{
    Integer index;
    Integer value;
    public Pair(int x,int y){
        index = x; value = y;
    }
}