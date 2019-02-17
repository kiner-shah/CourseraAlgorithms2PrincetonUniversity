/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Kiner Shah
 */

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;
import java.util.Arrays;

class DirectedEdge2D {
    public int v1, w1, v2, w2;
    public double weight;
    
    public void setEdge(int x1, int y1, int x2, int y2, double weight) {
        v1 = x1; w1 = y1;
        v2 = x2; w2 = y2;
        this.weight = weight;
    }
}

public class SeamCarver {
    private int pic_j, pic_i;
    private final double[][] energy_pix;
    private int[][] rgb_pic; 
    
    public SeamCarver(Picture picture) {               // create a seam carver object based on the given picture
        if (picture == null)
            throw new java.lang.IllegalArgumentException();
        
        pic_j = picture.width();
        pic_i = picture.height();
//        edu.princeton.cs.algs4.StdOut.println(pic_w + " by " + pic_h);
//        pic = new Picture(picture);
        energy_pix = new double[pic_j][pic_i];
        rgb_pic = new int[pic_j][pic_i];
        
        // Set energies at borders equal to 1000
        for (int j = 0; j < pic_j; j++) energy_pix[j][0] = 1000;
        for (int i = 0; i < pic_i; i++) energy_pix[0][i] = 1000;
        for (int j = 0; j < pic_j; j++) energy_pix[j][pic_i - 1] = 1000;
        for (int i = 0; i < pic_i; i++) energy_pix[pic_j - 1][i] = 1000;
        
        // Store color information from the picture
        for (int j = 0; j < pic_j; j++) {
            for (int i = 0; i < pic_i; i++) {
                rgb_pic[j][i] = picture.getRGB(j, i);
            }
        }
        // Calculate energies at pixels other than borders
        for (int j = 1; j < pic_j - 1; j++) {
            for (int i = 1; i < pic_i - 1; i++) {
                energy_pix[j][i] = energy(j, i);
            }
        }
    }
    public Picture picture() {                         // current picture
        Picture pic = new Picture(pic_j, pic_i);
        for (int i = 0; i < pic_j; i++) {
            for (int j = 0; j < pic_i; j++) {
                pic.setRGB(i, j, rgb_pic[i][j]);
            }
        }
        return pic;
    }
    public int width() {                           // width of current picture
        return pic_j;
    }
    public int height() {                          // height of current picture
        return pic_i;
    }
    private double sq_gradient(int row1, int col1, int row2, int col2) {
        int rgb1 = rgb_pic[row1][col1];
        int rgb2 = rgb_pic[row2][col2];
        
        int r1 = (rgb1 >> 16) & 0xFF;
        int g1 = (rgb1 >> 8) & 0xFF;
        int b1 = rgb1 & 0xFF;
        
        int r2 = (rgb2 >> 16) & 0xFF;
        int g2 = (rgb2 >> 8) & 0xFF;
        int b2 = rgb2 & 0xFF;
        
        return Math.pow(r1 - r2, 2.0) + Math.pow(g1 - g2, 2.0) + Math.pow(b1 - b2, 2.0);
    }
    public double energy(int x, int y) {              // energy of pixel at column x and row y
        if (x < 0 || x >= pic_j || y < 0 || y >= pic_i) {
            throw new java.lang.IllegalArgumentException();
        }
        
        if (x == 0 || x == pic_j - 1 || y == 0 || y == pic_i - 1) {
            return energy_pix[x][y];
        }
        
        double sq_gradient_x = sq_gradient(x - 1, y, x + 1, y);
        double sq_gradient_y = sq_gradient(x, y - 1, x, y + 1);
        
        return Math.sqrt(sq_gradient_x + sq_gradient_y);
    }
    private int[] findVerticalSeam(boolean transposeOn) {
        int[] vert_seam;
        double[][] distTo;
        DirectedEdge2D[][] edgeTo;
        if (transposeOn) {
            vert_seam = new int[pic_j];
            distTo = new double[pic_j][pic_i];
            edgeTo = new DirectedEdge2D[pic_j][pic_i];
            
            for (int j = 0; j < pic_j; j++) {
                for (int i = 0; i < pic_i; i++) {
                    edgeTo[j][i] = new DirectedEdge2D();
                    if (j == 0) {
                        distTo[j][i] = 0.0;
                        edgeTo[j][i].setEdge(j, i, j, i, 0.0);
                    } else {
                        distTo[j][i] = Double.POSITIVE_INFINITY;
                    }
                }
            }
            for (int j = 0; j < pic_j - 1; j++) {
                for (int i = 0; i < pic_i; i++) {
                    double val = distTo[j][i] + energy_pix[j + 1][i];
                    if (distTo[j + 1][i] > val) {
                        distTo[j + 1][i] = val;
                        edgeTo[j + 1][i].setEdge(j, i, j + 1, i, val);
                    }
                    if (i - 1 >= 0) {
                        val = distTo[j][i] + energy_pix[j + 1][i - 1];
                        if (distTo[j + 1][i - 1] > val) {
                            distTo[j + 1][i - 1] = val;
                            edgeTo[j + 1][i - 1].setEdge(j, i, j + 1, i - 1, val);
                        }
                    }
                    if (i + 1 < pic_i) {
                        val = distTo[j][i] + energy_pix[j + 1][i + 1];
                        if (distTo[j + 1][i + 1] > val) {
                            distTo[j + 1][i + 1] = val;
                            edgeTo[j + 1][i + 1].setEdge(j, i, j + 1, i + 1, val);
                        }
                    }
                }
            }
            double min_val = Double.POSITIVE_INFINITY;
            int i_ind = 0;
            for (int i = 0; i < pic_i; i++) {
//                StdOut.println(distTo[pic_j - 1][i]);
                if (min_val > distTo[pic_j - 1][i]) {
                    min_val = distTo[pic_j - 1][i];
                    i_ind = i;
                }
            }
            int j_ind = pic_j - 1;
            while (j_ind >= 0) {
                vert_seam[j_ind] = i_ind;
//                StdOut.printf("%d %d %7.2f %7.2f\n", j_ind, i_ind, distTo[j_ind][i_ind], energy_pix[j_ind][i_ind]);
                i_ind = edgeTo[j_ind][i_ind].w1;
                j_ind--;
            }
        }
        else {
            vert_seam = new int[pic_i];
            distTo = new double[pic_j][pic_i];
            edgeTo = new DirectedEdge2D[pic_j][pic_i];
            
            for (int i = 0; i < pic_j; i++) {
                for (int j = 0; j < pic_i; j++) {
                    edgeTo[i][j] = new DirectedEdge2D();
                    if (j == 0) {
                        distTo[i][j] = 0.0;
                        edgeTo[i][j].setEdge(i, j, i, j, 0.0);
                    } 
                    else distTo[i][j] = Double.POSITIVE_INFINITY;
                }
            }
            for (int j = 0; j < pic_i - 1; j++) {
                for (int i = 0; i <  pic_j; i++) {
                    double val = distTo[i][j] + energy_pix[i][j + 1];
                    if (distTo[i][j + 1] > val) {
                        distTo[i][j + 1] = val;
                        edgeTo[i][j + 1].setEdge(i, j, i, j + 1, val);
                    }
                    if (i - 1 >= 0) {
                        val = distTo[i][j] + energy_pix[i - 1][j + 1];
                        if (distTo[i - 1][j + 1] > val) {
                            distTo[i - 1][j + 1] = val;
                            edgeTo[i - 1][j + 1].setEdge(i, j, i - 1, j + 1, val);
                        }
                    }
                    if (i + 1 < pic_j) {
                        val = distTo[i][j] + energy_pix[i + 1][j + 1];
                        if (distTo[i + 1][j + 1] > val) {
                            distTo[i + 1][j + 1] = val;
                            edgeTo[i + 1][j + 1].setEdge(i, j, i + 1, j + 1, val);
                        }
                    }
                }
            }
            double min_val = Double.POSITIVE_INFINITY;
            int j_ind = 0;
            for (int i = 0; i < pic_j; i++) {
//                StdOut.println(distTo[i][pic_i - 1]);
                if (min_val > distTo[i][pic_i - 1]) {
                    min_val = distTo[i][pic_i - 1];
                    j_ind = i;
                }
            }
            int i_ind = pic_i - 1;
            while (i_ind >= 0) {
                vert_seam[i_ind] = j_ind;
//                StdOut.println(j_ind + " " + i_ind + " " + distTo[j_ind][i_ind] + " " + energy_pix[j_ind][i_ind]);
                j_ind = edgeTo[j_ind][i_ind].v1;
                i_ind--;
            }
        }
        return vert_seam;
    }
    public int[] findHorizontalSeam() {              // sequence of indices for horizontal seam
        return findVerticalSeam(true);
    }
    public int[] findVerticalSeam() {                // sequence of indices for vertical seam
        return findVerticalSeam(false);
    }
    public void removeHorizontalSeam(int[] seam) {  // remove horizontal seam from current picture
        if (seam == null || pic_i <= 1 || seam.length != pic_j) {
            throw new java.lang.IllegalArgumentException();
        }
        for (int i = 0; i < pic_j - 1; i++) {
            if (Math.abs(seam[i] - seam[i + 1]) > 1) {
                throw new java.lang.IllegalArgumentException();
            }
        }
        int[][] new_rgb = new int[pic_j][pic_i - 1];
        for (int i = 0; i < pic_j; i++) {
            if (seam[i] < 0 || seam[i] >= pic_i) throw new java.lang.IllegalArgumentException();
            for (int k = 0; k < seam[i]; k++) new_rgb[i][k] = rgb_pic[i][k];
            for (int k = seam[i] + 1; k < pic_i; k++) {
                new_rgb[i][k - 1] = rgb_pic[i][k];
//                energy_pix[i][k - 1] = energy(i, k - 1);
            }
        }
        rgb_pic = new_rgb;
        for (int j = 0; j < pic_j; j++) energy_pix[j][pic_i - 2] = 1000;
        pic_i--;
        
        for (int i = 0; i < pic_j; i++)  {
            if (seam[i] - 1 > 0) energy_pix[i][seam[i] - 1] = energy(i, seam[i] - 1);
            for (int k = seam[i] + 1; k < pic_i; k++) {
                energy_pix[i][k - 1] = energy(i, k - 1);
//                StdOut.printf("%7.2f ", energy_pix[i][k - 1]);
            } // StdOut.println();
        }  
    }
    public void removeVerticalSeam(int[] seam) {    // remove vertical seam from current picture
        if (seam == null || pic_j <= 1 || seam.length != pic_i) {
            throw new java.lang.IllegalArgumentException();
        }
        for (int i = 0; i < pic_i - 1; i++) {
            if (Math.abs(seam[i] - seam[i + 1]) > 1) {
                throw new java.lang.IllegalArgumentException();
            }
        }
        // Create a new pic with w = pic_j - 1, h = unchanged
        int[][] new_rgb = new int[pic_j - 1][pic_i];
        // for every row, remove the seam column
        for (int i = 0; i < pic_i; i++) {
            if (seam[i] < 0 || seam[i] >= pic_j) throw new java.lang.IllegalArgumentException();
            for (int k = 0; k < seam[i]; k++) new_rgb[k][i] = rgb_pic[k][i];
            for (int k = seam[i] + 1; k < pic_j; k++) {
                new_rgb[k - 1][i] = rgb_pic[k][i];
//                energy_pix[k - 1][i] = energy(k - 1, i);
            }
        }
        rgb_pic = new_rgb;
        for (int i = 0; i < pic_i; i++) energy_pix[pic_j - 2][i] = 1000;
        pic_j--;
        
        for (int i = 0; i < pic_i; i++)  {
            if (seam[i] - 1 > 0) energy_pix[seam[i] - 1][i] = energy(seam[i] - 1, i);
            for (int k = seam[i] + 1; k < pic_j; k++) {
                energy_pix[k - 1][i] = energy(k - 1, i);
//                StdOut.printf("%7.2f ", energy_pix[k - 1][i]);
            } // StdOut.println();
        }
    }
}
