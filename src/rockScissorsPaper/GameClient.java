package rockScissorsPaper;

import java.awt.*;

import java.net.*;

import java.io.*;

import java.awt.event.*;

public class GameClient extends Frame implements ActionListener{

  private TextArea msgView=new TextArea();

  private Button kawi, bawi, bo;        // ����, ����, ���� ���� ��ư

  private DataInputStream reader;

  private DataOutputStream writer;

  public static int KAWI=0;              // ������ ��Ÿ���� ���

  public static int BAWI=1;              // ������ ��Ÿ���� ���

  public static int BO=2;                // ���� ��Ÿ���� ���

  Socket socket;

  public GameClient(String title){            // ������

    super(title);

    msgView.setEditable(false);

 

    // �ʿ��� ��ư ��ü�� �����ϰ� ��ġ�Ѵ�.

    kawi=new Button("����");

    bawi=new Button("����");

    bo=new Button("��");

    add(msgView,"Center");

    Panel p=new Panel();

    p.add(kawi); p.add(bawi); p.add(bo);

    add(p, "South");

 

    // ��ư���� �̺�Ʈ�� ó���Ѵ�.

    kawi.addActionListener(this);

    bawi.addActionListener(this);

    bo.addActionListener(this);

 

    pack();

  }

  private void connect(){

    try{

      msgView.append("�������� ������ �õ��մϴ�.\n");

      socket=new Socket("127.0.0.1", 7777);

      msgView.append("������ �����մϴ�.\n");

     

      // ������ �ԡ���� ��Ʈ���� ��´�.

      reader=new DataInputStream(socket.getInputStream());

      writer=new DataOutputStream(socket.getOutputStream());

    }catch(Exception e){

      msgView.append("���� ����..");

    }

  }

  public void actionPerformed(ActionEvent ae){  // �׼� �̺�Ʈ ó��

    // ����ڰ� ������ �Ͱ� ������ ������ ��.

    int player=-1, server=-1;         // -1�� ���õ��� ���� ���¸� ��Ÿ����.

 

    // ����ڰ� ���� ��ư�� �ش��ϴ� ���� ����Ѵ�.

    if(ae.getSource()==kawi)

      player=KAWI;

    else if(ae.getSource()==bawi)

      player=BAWI;

    else if(ae.getSource()==bo)

      player=BO;

 

    // �ٸ� ��Ʈ�ѿ��� �߻��� �̺�Ʈ�̸� �޼ҵ带 ���� ���´�.

    if(player==-1)return;

   

    try{

      // "OK"�� ������ �����Ѵ�.

      writer.writeUTF("OK");

      writer.flush();

 

      // ������ ������ ��´�. 0~2 ������ ����

      server=reader.readInt();

    }catch(IOException ie){}

   

    // �º� ����� ����Ͽ� msgView�� ��Ÿ����.

    if(player==server)msgView.append("�����ϴ�.\n");

    else if(player>server || server-player==2)msgView.append("�̰���ϴ�.\n");

    else msgView.append("�����ϴ�.\n");

   

  }

 

  public static void main(String[] args){

    GameClient client=new GameClient("���������� ����");

    client.setVisible(true);

    client.connect();

  }

}