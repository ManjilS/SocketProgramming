package Networking;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MyClient implements Runnable {
	private JFrame frame = new JFrame("Chat Application");
    private JTextField textField = new JTextField(50);
    private JTextArea messageArea = new JTextArea(16, 50);

    public MyClient() {
        
        
        // Layout GUI
        textField.setEditable(false);
        messageArea.setEditable(false);
        frame.getContentPane().add(textField, BorderLayout.SOUTH);
        frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
        frame.pack();

        // Add Listeners
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                out.println(textField.getText());
                textField.setText("");
            }
        });
    }

     public static void main(String[] args){
         MyClient c= new MyClient();
         c.frame.setVisible(true);
         c.run();
     }
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done;
    
    @Override
     public void run(){
         try{
             client = new Socket("LocalHost",80);
             
             out = new PrintWriter(client.getOutputStream(),true);
             in = new BufferedReader(new InputStreamReader(client.getInputStream()));
             
             textField.setEditable(true);
             InputHandler inHandler = new InputHandler();
             Thread t = new Thread(inHandler);
             t.start();
             
             String inMsg;
             
             while((inMsg = in.readLine())!=null){
                 messageArea.append(inMsg+"\n");
             }
         }catch(Exception e){
             shutdown();
         }
     }
     public void shutdown(){
         done = true;
         try{
             in.close();
             out.close();
             if(!client.isClosed()){
                 client.close();
             }
         }catch(Exception e){
                      shutdown();  
               }
         }
     
     class InputHandler implements Runnable{
         
         @Override
         public void run(){
             try{
                 BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
                 while(!done){
                     String msg = inReader.readLine();
                     
                     if(msg.equals("/quit")){
                         out.println(msg);
                         inReader.close();
                         shutdown();
                     }else{
                         out.println(msg);
                     }
                 }
             }catch(Exception e){
                 
             }
         }
     }
     
    
}
