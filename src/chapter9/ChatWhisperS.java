package chapter9;


//Step 4
//Ŭ���̾�Ʈ ���� ä�ÿ��� Ư�� Ŭ���̾�Ʈ���� �ӼӸ� ����
//������ �������� �ӼӸ� ����
///w ������̵� ��ȭ��
//package Server;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.awt.*;
import java.awt.event.*;

public class ChatWhisperS extends Frame {
	TextArea display;
	Label info;
	List<Map.Entry<ServerThread, Boolean>> list;
	Hashtable<String, ServerThread> hash;
	public ServerThread SThread;

	public ChatWhisperS() {
		super("����");
		info = new Label();
		add(info, BorderLayout.CENTER);
		display = new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
		display.setEditable(false);
		add(display, BorderLayout.SOUTH);
		addWindowListener(new WinListener());
		setSize(350, 250);
		setVisible(true);
	}

	public void runServer() {
		ServerSocket server;
		Socket sock;
		ServerThread SThread;
		try {
			server = new ServerSocket(5000, 100);
			hash = new Hashtable<String, ServerThread>();
			list = new ArrayList<Map.Entry<ServerThread, Boolean>>();
			try {
				while (true) {
					sock = server.accept();
					SThread = new ServerThread(this, sock, display, info);
					SThread.start();
					info.setText(sock.getInetAddress().getHostName() + " ������ Ŭ���̾�Ʈ�� �����");
				}
			} catch (IOException ioe) {
				server.close();
				System.out.println("haha");
				ioe.printStackTrace();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}

	public static void main(String args[]) {
		ChatWhisperS s = new ChatWhisperS();
		s.runServer();
	}

	class WinListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	}
}

class ServerThread extends Thread {
	Socket sock;
	BufferedWriter output;
	BufferedReader input;
	TextArea display;
	Label info;
	TextField text;
	String clientdata;
	String serverdata = "";
	ChatWhisperS cs;
	String[] arrid = new String[100];
	boolean check = false;

	private static final String SEPARATOR = "|";
	private static final int REQ_LOGON = 1001;
	private static final int REQ_SENDWORDS = 1021;
	private static final int REQ_WISPERSEND = 1022;
	private static final int REQ_LOGOUT = 1002;
	private static final int REQ_QUIT = 1003;
	private Set<String> allid;

	public ServerThread(ChatWhisperS c, Socket s, TextArea ta, Label l) {
		sock = s;
		display = ta;
		info = l;
		cs = c;
		try {
			input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			output = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void run() {
		try {
			cs.list.add(new AbstractMap.SimpleEntry<ServerThread, Boolean>(this, false));
			while ((clientdata = input.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(clientdata, SEPARATOR);
				int command = Integer.parseInt(st.nextToken());
				switch (command) {
				case REQ_LOGON: {
					String ID = st.nextToken();
					allid = cs.hash.keySet();
					allid.toArray(arrid);
					for (int i = 0; i < arrid.length; i++) {
						if (arrid[i] != null) {
							if (arrid[i].compareTo(ID) == 0)
								check = true;
						}
					}
					cs.hash.put(this.toString(), this);
					if (!check) {
						cs.hash.remove(this.toString());
						cs.hash.put(ID, this); // �ؽ� ���̺��� ���̵�� �����带 �����Ѵ�
						for (int i = 0; i < cs.list.size(); i++) {
							if (cs.list.get(i).getKey().getName().compareTo(cs.hash.get(ID).getName()) == 0) {
								cs.list.get(i).setValue(true);
								ServerThread SThread1 = (ServerThread) cs.list.get(i).getKey();
								SThread1.output.write("�α��� �Ϸ�.\r\n");
								SThread1.output.flush();
								display.append("Ŭ���̾�Ʈ�� " + ID + "(��)�� �α��� �Ͽ����ϴ�.\r\n");
							}
						}
					} else {
						for (int i = 0; i < cs.list.size(); i++) {
							if (cs.list.get(i).getKey().getName()
									.compareTo(cs.hash.get(this.toString()).getName()) == 0) {
								ServerThread SThread1 = (ServerThread) cs.list.get(i).getKey();
								SThread1.output.write("(����)�ߺ��� ID\r\n");
								SThread1.output.flush();
								display.append("�ߺ��� " + ID + "�Դϴ�.\r\n");
							}
						}
					}
					check = false;
					break;
				}
				case REQ_SENDWORDS: {
					String ID = st.nextToken();
					String message = st.nextToken();
					display.append(ID + " : " + message + "\r\n");
					for (int i = 0; i < cs.list.size(); i++) {
						if (cs.list.get(i).getValue()) {
							ServerThread SThread = (ServerThread) cs.list.get(i).getKey();
							SThread.output.write(ID + " : " + message + "\r\n");
							SThread.output.flush();
						}
					}
					break;
				}
				case REQ_WISPERSEND: {
					String ID = st.nextToken();
					String WID = st.nextToken();
					String message = st.nextToken();
					display.append(ID + " -> " + WID + " : " + message + "\r\n");
					ServerThread SThread = (ServerThread) cs.hash.get(ID);
					// �ؽ����̺����� �ӼӸ� �޽����� ������ Ŭ���̾�Ʈ�� �����带 ����
					SThread.output.write(ID + " -> " + WID + " : " + message + "\r\n");
					// �ӼӸ� �޽����� ������ Ŭ���̾�Ʈ�� ������
					SThread.output.flush();
					SThread = (ServerThread) cs.hash.get(WID);
					// �ؽ����̺����� �ӼӸ� �޽����� ������ Ŭ���̾�Ʈ�� �����带 ����
					SThread.output.write(ID + " : " + message + "\r\n");
					// �ӼӸ� �޽����� ������ Ŭ���̾�Ʈ�� ������
					SThread.output.flush();
					break;
				}
				case REQ_LOGOUT: {
					String ID = st.nextToken();
					display.append(ID + "�� �α׾ƿ� �Ǿ����ϴ�.\r\n");
					cs.hash.remove(ID);
					for (int i = 0; i < cs.list.size(); i++) {
						if (cs.list.get(i).getValue()) {
							ServerThread SThread = (ServerThread) cs.list.get(i).getKey();
							SThread.output.write(ID + "�� �α׾ƿ��Ǿ����ϴ�.\r\n");
							SThread.output.flush();
						}
					}
					break;
				}
				case REQ_QUIT: {
					String ID = st.nextToken();
					if (ID != null) {
						for (int i = 0; i < cs.list.size(); i++) {
							if (cs.list.get(i).getKey().getName().compareTo(cs.hash.get(ID).getName()) == 0) {
								display.append(ID + "�� �α׾ƿ� �Ǿ����ϴ�.\r\n");
								cs.hash.remove(ID);
								cs.list.remove(i);
								break;
							}
						}
					} else {
						for (int i = 0; i < cs.list.size(); i++) {
							if (cs.list.get(i).getKey().getName()
									.compareTo(cs.hash.get(this.toString()).getName()) == 0) {
								display.append(cs.hash.get(this.toString()).getName());
								cs.hash.remove(cs.hash.get(this.toString()).getName());
								cs.list.remove(i);
								break;
							}
						}
					}
				}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			sock.close();
		} catch (IOException ea) {
			ea.printStackTrace();
		}
	}
}