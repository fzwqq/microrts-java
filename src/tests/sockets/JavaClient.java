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
  * @author mro


  */
 public class JavaClient {
     static int episodes = 100000;
     static int MAXCYCLES = 500;
     static int PERIOD = 5;
     static boolean DEBUG = true;
     static String serverIP = "127.0.0.1";
     static int serverPort = 9898;
//   SocketAI.DEBUG = 1;
     public static void runEpisodes() throws Exception {
         UnitTypeTable utt = new UnitTypeTable();
         SocketAI ai1 = new SocketAI(100,0, serverIP, serverPort, SocketAI.LANGUAGE_JSON, utt);
         AI ai2 = new WorkerRush(utt);
         //        AI ai2 = new PassiveAI();
         for(int i = 0;i<episodes;++i) {

//           JOptionPane.showMessageDialog(null, System.getProperty("user.dir"));
//           GameState clonedGs =  gs.clone();   //s for reset the game
             PhysicalGameState pgs = PhysicalGameState.load("/home/mro/IdeaProjects/microrts-java/maps/8x8/bases8x8workersmelee.xml", utt);
             GameState gs = new GameState(pgs, utt);
             JFrame w = PhysicalGameStatePanel.newVisualizer(gs, 640, 640, false, PhysicalGameStatePanel.COLORSCHEME_WHITE);
             w.setFocusable(false);
             ai1.myReset(gs,0);
             ai2.reset();
             boolean gameover = false;
             long nextTimeToUpdate = System.currentTimeMillis() + PERIOD;
             do{

                 if (System.currentTimeMillis()>=nextTimeToUpdate) {

                     PlayerAction pa1 = ai1.getAction(0, gs);
                     PlayerAction pa2 = ai2.getAction(1, gs);
                     gs.issueSafe(pa1);
                     gs.issueSafe(pa2);
                     // simulate:
                     gameover = gs.cycle();

                     ai1.sendGameState(gs, 0,false);
//                   ai2.send
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
     public static void main(String args[]) throws Exception {
         runEpisodes();
     }
 }
