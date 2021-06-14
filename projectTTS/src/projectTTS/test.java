package projectTTS;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.protobuf.ByteString;

public class test extends JFrame  {

    JLabel label1 = new JLabel();
	//AudioConfig audioConfig =AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.LINEAR16).build();
	RoundedButton wordButton = new RoundedButton("단어 시험"); //단어 시험 버튼
	RoundedButton meanButton = new RoundedButton("뜻 시험"); // 뜻 시험 버튼
	RoundedButton submitButton = new RoundedButton("제출"); // submit 버튼
	JTextField testField = new JTextField(); // 입력 텍스트필드
	JLabel wordLabel = new JLabel(); // 문제 띄우는 라벨
	
	Object [][]wrong = new Object[100][3];
	JScrollPane scrolltable = new JScrollPane(); // 시험 끝에 정답 확인하는 테이블
	JProgressBar cntBar = new JProgressBar(); // 진행 게이지바
	ImageIcon iconO = new ImageIcon("O.png");
	ImageIcon iconX = new ImageIcon("XX.png");
	ImageIcon iconNote = new ImageIcon("blue.jpg");
	ImageIcon iconNote1 = new ImageIcon("blue1.jpg");
	int testcnt;
	int[] currenttestnum = new int[100];
	int testcount;
	int result;
	static int WordMean=-1;
	List<Integer> array = new ArrayList<>(); // 시험칠 단어를 저장할 list
	test() throws IOException, LineUnavailableException, UnsupportedAudioFileException{
		
		JRootPane  rootPane  =  this.getRootPane();
		rootPane.setDefaultButton(submitButton);
		Object[] column = {"(O,X)","단어","뜻"};
		
		// 시험 끝에 정답 확인하는 테이블 생성
		JTable wrongTable = new JTable(wrong,column) {
			@Override
			public Class getColumnClass(int column) {
	            switch (column) {
	                case 0:
	                    return Icon.class;
	                case 1:
	                    return String.class;
	                default:
	                    return String.class;
	            }
	        }
			
			@Override
			public boolean isCellEditable(int row, int col) {
		        return false;
		    }
		};
		wrongTable.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		wrongTable.setFont(new Font("돋움체", Font.BOLD, 20));
		wrongTable.setBackground(new Color(MyFrame.wordColor[0]));
		wrongTable.getColumnModel().getColumn(0).setPreferredWidth(50);
		wrongTable.getColumnModel().getColumn(1).setPreferredWidth(150);
		wrongTable.getColumnModel().getColumn(2).setPreferredWidth(150);
		wrongTable.setRowHeight(40);
		scrolltable = new JScrollPane(wrongTable);
		scrolltable.setVisible(false);
		scrolltable.setBounds(0,50,465,700);
        	this.add(scrolltable);

		wordButton.setBounds(135, 205, 200, 100);
		meanButton.setBounds(135, 355, 200, 100);
		submitButton.setBounds(143, 600, 190, 74);
		testField.setBounds(143, 420, 195, 70);
		wordLabel.setBounds(0, 187, 480, 80);
		wordLabel.setHorizontalAlignment(JLabel.CENTER);
		wordLabel.setForeground(Color.BLACK);
		wordLabel.setBackground(Color.WHITE);
		cntBar.setBounds(300,30,150,40);
		 
		// 글자 폰트
		wordButton.setFont(new Font("함초롬돋움", Font.BOLD, 30));
		meanButton.setFont(new Font("함초롬돋움", Font.BOLD, 30));
		testField.setFont(new Font("함초롬돋움", Font.BOLD, 30));
		submitButton.setFont(new Font("Comic Sans", Font.BOLD, 30));
		wordLabel.setFont(new Font("Comic Sans", Font.BOLD, 30));
		cntBar.setFont(new Font("Comic Sans", Font.BOLD, 30));
		
		testField.setVisible(false);
		submitButton.setVisible(false);
		wordLabel.setVisible(false);
		cntBar.setVisible(false);
		
		// 진행 게이지바 색깔
		cntBar.setForeground(new Color(0xade86d));
		cntBar.setBackground(Color.WHITE);
		
		// 단어시험 버튼 눌렀을 때
		wordButton.addActionListener(e->{
	        label1.setIcon(iconNote1);
			WordMean=0;
			testcount=0;
			testcnt=0;
			wordButton.setVisible(false);
			meanButton.setVisible(false);
			testField.setVisible(true);
			submitButton.setVisible(true);
			wordLabel.setVisible(true);
			testField.setHorizontalAlignment(JTextField.CENTER);
			for(int i=0;i<MyFrame.SubjectWordCnt[MyFrame.currentSubject];i++) {
				if(MyFrame.SubjectWord[MyFrame.currentSubject][i][3].equals(true)) {
					array.add(i);
					testcnt++;
				}
			}
			cntBar.setValue(((testcount+1)*100)/testcnt);
			cntBar.setString(testcount+1+"/"+testcnt);
			cntBar.setStringPainted(true);
			cntBar.setVisible(true);
			Collections.shuffle(array); // 랜덤으로 시험치기 위한 shuffle 함수
			wordLabel.setText((String) MyFrame.SubjectWord[MyFrame.currentSubject][array.get(0)][2]);
			
			// 시험치는 부분 tts로 읽어주기
			try {
				tts((String) MyFrame.SubjectWord[MyFrame.currentSubject][array.get(0)][2], 1);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		
		// 뜻시험 버튼 눌렀을 때
		meanButton.addActionListener(e->{
	        label1.setIcon(iconNote1);
			WordMean=1;
			testcount=0;
			testcnt=0;
			wordButton.setVisible(false);
			meanButton.setVisible(false);
			testField.setVisible(true);
			submitButton.setVisible(true);
			wordLabel.setVisible(true);
			
			for(int i=0;i<MyFrame.SubjectWordCnt[MyFrame.currentSubject];i++) {
				if(MyFrame.SubjectWord[MyFrame.currentSubject][i][3].equals(true)) {
					array.add(i);
					testcnt++;
				}
			}
			cntBar.setValue(((testcount+1)*100)/testcnt);
			cntBar.setString(testcount+1+"/"+testcnt);
			cntBar.setStringPainted(true);
			cntBar.setVisible(true);
			Collections.shuffle(array); // 랜덤으로 시험치기 위한 shuffle 함수
			wordLabel.setText((String) MyFrame.SubjectWord[MyFrame.currentSubject][array.get(0)][1]);
			
			// 시험치는 부분 tts로 읽어주기
			try {
				tts((String) MyFrame.SubjectWord[MyFrame.currentSubject][array.get(0)][1], 0);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
				
		submitButton.addActionListener(f->{
			// 단어 시험 때 submit 버튼 눌렀을 때
			if(WordMean==0) {
				if(MyFrame.SubjectWord[MyFrame.currentSubject][array.get(testcount)][1].equals(testField.getText())) {
					wrongTable.setValueAt(iconO, testcount, 0);
					wrong[testcount][1]=MyFrame.SubjectWord[MyFrame.currentSubject][array.get(testcount)][1];
					wrong[testcount][2]=MyFrame.SubjectWord[MyFrame.currentSubject][array.get(testcount++)][2];
				}
				else {
					wrongTable.setValueAt(iconX, testcount, 0);
					wrong[testcount][1]=MyFrame.SubjectWord[MyFrame.currentSubject][array.get(testcount)][1];
					wrong[testcount][2]=MyFrame.SubjectWord[MyFrame.currentSubject][array.get(testcount++)][2];
				}
				cntBar.setValue(((testcount+1)*100)/testcnt);
				cntBar.setString(testcount+1+"/"+testcnt);
				testField.setText("");
				
				// test 끝날때
				if(testcnt==testcount) {
					cntBar.setVisible(false);
					testField.setVisible(false);
					scrolltable.setVisible(true);
					submitButton.setVisible(false);
				}
				else {
					wordLabel.setText((String) MyFrame.SubjectWord[MyFrame.currentSubject][array.get(testcount)][2]);
					// 시험치는 부분 tts로 읽어주기
					try {
						tts((String) MyFrame.SubjectWord[MyFrame.currentSubject][array.get(testcount)][2], 1);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
			// 뜻 시험 때 submit 버튼 눌렀을 때
			if(WordMean==1) {
				if(MyFrame.SubjectWord[MyFrame.currentSubject][array.get(testcount)][2].equals(testField.getText())) {
					wrongTable.setValueAt(iconO, testcount, 0);
					wrong[testcount][1]=MyFrame.SubjectWord[MyFrame.currentSubject][array.get(testcount)][1];
					wrong[testcount][2]=MyFrame.SubjectWord[MyFrame.currentSubject][array.get(testcount++)][2];
				}
				else {
					wrongTable.setValueAt(iconX, testcount, 0);
					wrong[testcount][1]=MyFrame.SubjectWord[MyFrame.currentSubject][array.get(testcount)][1];
					wrong[testcount][2]=MyFrame.SubjectWord[MyFrame.currentSubject][array.get(testcount++)][2];

				}
				cntBar.setValue(((testcount+1)*100)/testcnt);
				cntBar.setString(testcount+1+"/"+testcnt);
				testField.setText("");
				
				// test 끝날때
				if(testcnt==testcount) {
					cntBar.setVisible(false);
					testField.setVisible(false);
					scrolltable.setVisible(true);
					submitButton.setVisible(false);
				}
				else {
					wordLabel.setText((String) MyFrame.SubjectWord[MyFrame.currentSubject][array.get(testcount)][1]);
					// 시험치는 부분 tts로 읽어주기
					try {
						tts((String) MyFrame.SubjectWord[MyFrame.currentSubject][array.get(testcount)][1], 0);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		this.setIconImage(MyFrame.icon2.getImage());
       		label1.setIcon(iconNote);
        	label1.setBounds(0,0,480,854);
        	label1.setVisible(true);
        	JPanel panel = new JPanel();
		panel.setVisible(true);
		panel.setBounds(0,0,480,854);
		this.add(panel);
		panel.add(label1);
		this.setResizable(false);
		this.setSize(480,854);
		this.setLayout(null);
		this.setVisible(true);
		
		
		label1.add(wordButton);
		label1.add(meanButton);
		label1.add(testField);
		label1.add(submitButton);
		label1.add(wordLabel);
		label1.add(cntBar);

		// test 종료시
		this.addWindowListener(new java.awt.event.WindowAdapter() {
	        @Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent){
	        	wordButton.setVisible(true);
				meanButton.setVisible(true);
				testField.setVisible(false);
				submitButton.setVisible(false);
				wordLabel.setVisible(false);
				wordLabel.setHorizontalAlignment(JLabel.CENTER);
				wordLabel.setText("");
			}
	    });
	}
	void tts(String subjectWord, int b)throws Exception {
		try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
			VoiceSelectionParams voice;
			System.out.println(b);
			if (b==1) {
				voice =VoiceSelectionParams.newBuilder().setLanguageCode("ko-KR")
						.setSsmlGender(SsmlVoiceGender.NEUTRAL).build();
			}
			else {
				voice =VoiceSelectionParams.newBuilder().setLanguageCode("en-US")
						.setSsmlGender(SsmlVoiceGender.NEUTRAL).build();
			}
			SynthesisInput input = SynthesisInput.newBuilder().setText(subjectWord).build();
			SynthesizeSpeechResponse response =textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);
			ByteString audioContents = response.getAudioContent();
			try (OutputStream out = new FileOutputStream("output.wav")) {
				out.write(audioContents.toByteArray());
			}
		}
		File file = new File("output.wav");
		AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
		Clip clip = AudioSystem.getClip();
		clip.open(audioStream);
		clip.start();
	}
}
