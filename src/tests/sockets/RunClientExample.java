 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tests.sockets;

import ai.abstraction.WorkerRush;
import ai.abstraction.WorkerRushPlusPlus;
import ai.core.AI;
import ai.*;
import ai.socket.SocketAI;
import gui.PhysicalGameStatePanel;

import javax.swing.*;

import rts.GameState;
import rts.PhysicalGameState;
import rts.PlayerAction;
import rts.units.UnitTypeTable;

/**
 *
 * @author santi
 * 
 * Once you have the server running (for example, run "RunServerExample.java"),
 * set the proper IP and port in the variable below, and run this file.
 * One of the AIs (ai1) is run remotely using the server.
 * 
 * Notice that as many AIs as needed can connect to the same server. For
 * example, uncomment line 44 below and comment 45, to see two AIs using the same server.
 * 
 */
public class RunClientExample {
    public static void main(String args[]) throws Exception {
        int episodes = 100000;
        int MAXCYCLES = 500;
        int PERIOD = 5;
        boolean DEBUG = true;
        String serverIP = "127.0.0.1";
        int serverPort = 9898;
//      SocketAI.DEBUG = 1;
        UnitTypeTable utt = new UnitTypeTable();
        AI ai1 = new SocketAI(100,0, serverIP, serverPort, SocketAI.LANGUAGE_JSON, utt);
        AI ai2 = new WorkerRush(utt);
//        AI ai2 = new PassiveAI();
        for(int i = 0;i<episodes;++i) {
    //                JOptionPane.showMessageDialog(null, System.getProperty("user.dir"));
            PhysicalGameState pgs = PhysicalGameState.load("/home/mro/IdeaProjects/microrts-java/maps/8x8/bases8x8workersmelee.xml", utt);
            GameState gs = new GameState(pgs, utt);
//            GameState clonedGs =  gs.clone();   // for reset the game
            ai1.reset();
            ai2.reset();
            boolean gameover = false;
            JFrame w = PhysicalGameStatePanel.newVisualizer(gs, 640, 640, false, PhysicalGameStatePanel.COLORSCHEME_WHITE);
            long nextTimeToUpdate = System.currentTimeMillis() + PERIOD;
            do{
                if (System.currentTimeMillis()>=nextTimeToUpdate) {
                    long start = System.currentTimeMillis();
                    PlayerAction pa1 = ai1.getAction(0, gs);
                    long end = System.currentTimeMillis();
//                    System.out.println(end-start);
                    PlayerAction pa2 = ai2.getAction(1, gs);
                    gs.issueSafe(pa1);
                    gs.issueSafe(pa2);

                    // simulate:
                    gameover = gs.cycle();
                    System.out.println(gameover);
                    if (DEBUG){
                        w.repaint();
                    }
                    nextTimeToUpdate+=PERIOD;
                } else {
                    try {
                        Thread.sleep(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }while(!gameover && gs.getTime() < MAXCYCLES);
//            System.out.println(gs.winner());
            ai1.gameOver(gs.winner());
            ai2.gameOver(gs.winner());
            w.dispose();
        }

    }    
}
