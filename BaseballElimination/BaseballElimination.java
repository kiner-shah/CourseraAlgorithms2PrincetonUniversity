/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Kiner Shah
 */
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
//import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;
import java.util.ArrayList;
import java.util.HashMap;

// For testing
/*
class FF {

    private boolean[] marked;	// true if s->v path in residual network
    private FlowEdge[] edgeTo;	// last edge on s->v path
    private double value;	// value of flow

    public FF(FlowNetwork G, int s, int t) {
        value = 0.0;
        while (hasAugmentingPathTo(G, s, t)) {
            double bottle = Double.POSITIVE_INFINITY;
            // compute bottleneck capacity
            for (int v = t; v != s; v = edgeTo[v].other(v)) {
                bottle = Math.min(bottle, edgeTo[v].residualCapacityTo(v));
            }
            // augment flow
            for (int v = t; v != s; v = edgeTo[v].other(v)) {
                edgeTo[v].addResidualFlowTo(v, bottle);
                StdOut.println(v + " " + edgeTo[v].flow());
            }
            value += bottle;
            StdOut.println(value + "\n-----");
        }
    }

    private boolean hasAugmentingPathTo(FlowNetwork G, int s, int t) {
        edgeTo = new FlowEdge[G.V()];
        marked = new boolean[G.V()];

        Queue<Integer> q = new Queue<Integer>();
        q.enqueue(s);
        marked[s] = true;

        while (!q.isEmpty()) {
            int v = q.dequeue();
            for (FlowEdge e : G.adj(v)) {
                int w = e.other(v);
                if (e.residualCapacityTo(w) > 0.0 && !marked[w]) {
                    StdOut.println(v + " ADJ " + w + " res cap = " + e.residualCapacityTo(w));
                    marked[w] = true;
                    edgeTo[w] = e;
                    q.enqueue(w);
                }
            }
        }
        return marked[t];
    }

    public double value() {
        return value;
    }

    public boolean inCut(int v) {// is v reachable from s in residual network?
        return marked[v];
    }
}
*/
public class BaseballElimination {
    private final int noOfTeams;
    private final HashMap<String, Integer> teamNameNumber;
    private final HashMap<String, Iterable<String>> eliminated;
    private final int[] w, l, r;
    private final int[][] g;
    
    public BaseballElimination(String filename) { // create a baseball division from given filename in format specified below
        In in = new In(filename);
        noOfTeams = Integer.parseInt(in.readLine());
        teamNameNumber = new HashMap<String, Integer>();
        eliminated = new HashMap<String, Iterable<String>>();
        w = new int[noOfTeams];
        l = new int[noOfTeams];
        r = new int[noOfTeams];
        g = new int[noOfTeams][noOfTeams];
        int lineNo = 0;
        while (in.hasNextLine()) {
            String splitted[] = in.readLine().trim().split("\\s+");
//            StdOut.println(splitted.length);
            teamNameNumber.put(splitted[0].trim(), lineNo);
            w[lineNo] = Integer.parseInt(splitted[1].trim());
            l[lineNo] = Integer.parseInt(splitted[2].trim());
            r[lineNo] = Integer.parseInt(splitted[3].trim());
            for (int j = 0; j < noOfTeams; j++) {
                g[lineNo][j] = Integer.parseInt(splitted[4 + j].trim());
            }
            lineNo++;
        }
        for (String team : teamNameNumber.keySet()) {
            ArrayList<String> te = trivialElimination(team);
            if (te.isEmpty()) {
                ArrayList<String> nte = nonTrivialElimination(team);
                if (!nte.isEmpty())
                    eliminated.put(team, nonTrivialElimination(team));
            }
            else eliminated.put(team, te);
        }
    }
    private ArrayList<String> trivialElimination(String teamName) {
        int targetId = teamNameNumber.get(teamName);
        ArrayList<String> al = new ArrayList<String>();
        for (String team : teamNameNumber.keySet()) {
            int id = teamNameNumber.get(team);
            if (id != targetId && w[targetId] + r[targetId] < w[id])
                al.add(team);
        }
        return al;
    }
    private ArrayList<String> nonTrivialElimination(String teamName) {
        int teamNumber = teamNameNumber.get(teamName);
        int networkSize = (noOfTeams - 1) + ((noOfTeams - 1) * (noOfTeams - 2)) / 2 + 2;
        HashMap<String, Integer> graphNameID = new HashMap<String, Integer>();
        HashMap<Integer, String> graphIDName = new HashMap<Integer, String>();
        FlowNetwork fn = new FlowNetwork(networkSize);
        int cnt = 0;
        graphNameID.put("target", networkSize - 1);
        graphNameID.put("source", networkSize - 2);
        // all teams [0, noOfTeams - 1] to t [networkSize - 1]
        for (String team : teamNameNumber.keySet()) {
            if (!team.equals(teamName)) {
                int id = teamNameNumber.get(team);
                if (w[teamNumber] + r[teamNumber] - w[id] >= 0)
                    fn.addEdge(new FlowEdge(cnt, networkSize - 1, w[teamNumber] + r[teamNumber] - w[id]));
                graphNameID.put(team, cnt);
                graphIDName.put(cnt, team);
                cnt++;
            }
        }
        // s [networkSize - 2] to all pairs of teams [noOfTeams, networkSize - 3]
        int i = 0;
        for (String team1 : teamNameNumber.keySet()) {
            int j = i;
            if (!team1.equals(teamName)) {
                int c = 0;
                for (String team2 : teamNameNumber.keySet()) {
                    if (!team2.equals(teamName) && c > j) {
                        int gamesLeft = g[teamNameNumber.get(team1)][teamNameNumber.get(team2)];
//                        StdOut.println(team1 + " " + team2);
//                        StdOut.printf("%d %d %d\n", networkSize - 2, cnt, gamesLeft);
                        fn.addEdge(new FlowEdge(networkSize - 2, cnt, gamesLeft));
                        int ind1 = graphNameID.get(team1);
                        int ind2 = graphNameID.get(team2);
                        fn.addEdge(new FlowEdge(cnt, ind1, Double.POSITIVE_INFINITY));
                        fn.addEdge(new FlowEdge(cnt, ind2, Double.POSITIVE_INFINITY));
                        graphNameID.put(team1 + "-" + team2, cnt);
                        graphIDName.put(cnt, team1 + "-" + team2);
//                        StdOut.println(ind1 + " " + ind2 + " " + team1 + "-" + team2 + " " + cnt);
                        cnt++;
                    }
                    c++;
                }
            }
            i++;
        }

        ArrayList<String> la = new ArrayList<String>();
        FordFulkerson ff = new FordFulkerson(fn, networkSize - 2, networkSize - 1);
       
        for (int k = 0; k < noOfTeams - 1; k++) {
            if (ff.inCut(k)) {
                la.add(graphIDName.get(k));
            }
        }   
        return la;
    }
    public int numberOfTeams() { // number of teams
        return noOfTeams;
    }

    public Iterable<String> teams() { // all teams
        return teamNameNumber.keySet();
    }

    public int wins(String team) { // number of wins for given team
        if (!teamNameNumber.containsKey(team))
            throw new java.lang.IllegalArgumentException();
        int index = teamNameNumber.get(team);
        return w[index];
    }

    public int losses(String team) { // number of losses for given team
        if (!teamNameNumber.containsKey(team))
            throw new java.lang.IllegalArgumentException();
        int index = teamNameNumber.get(team);
        return l[index];
    }

    public int remaining(String team) { // number of remaining games for given team
        if (!teamNameNumber.containsKey(team))
            throw new java.lang.IllegalArgumentException();
        int index = teamNameNumber.get(team);
        return r[index];
    }

    public int against(String team1, String team2) { // number of remaining games between team1 and team2
        if (!teamNameNumber.containsKey(team1) || !teamNameNumber.containsKey(team2))
            throw new java.lang.IllegalArgumentException();
        int index1 = teamNameNumber.get(team1);
        int index2 = teamNameNumber.get(team2);
        return g[index1][index2];
    }

    public boolean isEliminated(String team) { // is given team eliminated?
        if (!teamNameNumber.containsKey(team))
            throw new java.lang.IllegalArgumentException();
//        StdOut.println(team + " " + eliminated.containsKey(team));
        return eliminated.containsKey(team);
    }
    private double calculateCOE(Iterable<String> eliminators) {
        double sumW = 0.0, sumR = 0.0;
        int cnt = 0;
        for (String x : eliminators) {
            int index = teamNameNumber.get(x);
            sumW += w[index];
            cnt++;
            for (String y : eliminators) {
                if (!y.equals(x)) {
                    int index2 = teamNameNumber.get(y);
                    sumR += g[index][index2];
                }
            }
        }
        StdOut.println(sumW + " " + (sumR / 2.0));
        return (sumW + sumR / 2.0) / cnt;
    }
    public Iterable<String> certificateOfElimination(String team) { // subset R of teams that eliminates given team; null if not eliminated
        if (!teamNameNumber.containsKey(team))
            throw new java.lang.IllegalArgumentException();
        if (!isEliminated(team))
            return null;
        else {
//            double coe = calculateCOE(eliminated.get(team));
//            StdOut.printf("COE = %.3f\n", coe);
//            if (coe > w[teamNameNumber.get(team)] + r[teamNameNumber.get(team)])
//                StdOut.println("CORRECT");
            return eliminated.get(team);
        }
    }
    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            } else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
