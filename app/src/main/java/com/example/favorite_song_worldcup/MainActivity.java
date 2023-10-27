package com.example.favorite_song_worldcup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

// 음악을 재생하기 위해 MediaPlayer 클래스를 import
import android.media.MediaPlayer;

import androidx.appcompat.app.AppCompatActivity;

import java.util.LinkedList;
import java.util.Queue;

public class MainActivity extends AppCompatActivity {

    public static ImageButton imgtop;
    public static ImageButton imgbot;
    public static TextView stage;
    public static TextView textview;
    public static ProgressBar progressBar;
    // 위쪽 플래이 버튼, 아래쪽 플래이 버튼을 따로 생각해 준다
    private static MediaPlayer mediaPlayerTop;
    private static MediaPlayer mediaPlayerBottom;
    // 다음 라운드로 넘어갔을때 여전히 이전 라운드의 노래가 재생되는 이슈 방지. 라운드를 직접 카운트 할 예정
    private static int currentRound = 0;
    // 8강 생성시 랜덤생성 위한, 배열

    // 8강,4강,결승에서 선택한 값들을 순서대로 저장하기 위한 큐.
    Queue<Integer> queue = new LinkedList<>();
    public static boolean[] pics = new boolean[16];
    // 현재 선택할 수 있는 index 2개 static으로 저장.
    static int[] window= new int[2];

    // 선택할 때마다, 2씩 더해서 현재 위치 추적. 0~7 -> 8강, 8~11 : 4강, 12~ 결승
    static int pics_value = 0;

    // 위쪽 재생 버튼을 눌렀을때, raw 디렉토리에서 해당 이미지에 맞는 노래를 재생한다.
    private void playMusicTop(Context context, String musicFileName) {
        // 이전에 재생중인 음악이 있다면 중지, 이거 없으면 버튼 두번 누르면 노래 두번나옴
        stopMusicTop();
        //재생할 음악 찾아서 재생
        int resID = context.getResources().getIdentifier(musicFileName, "raw", context.getPackageName());
        mediaPlayerTop = MediaPlayer.create(context, resID);
        // 예외처리 후 음악 재생
        if (mediaPlayerTop != null) {
            mediaPlayerTop.start();
            mediaPlayerTop.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                // 음악 재생이 한번 끝나면 재생정지
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopMusicTop();
                }
            });
        }
    }

    // 아래쪽 재생 버튼. 위쪽 버튼과 같은 방식
    private void playMusicBottom(Context context, String musicFileName) {
        stopMusicBottom();
        int resID = context.getResources().getIdentifier(musicFileName, "raw", context.getPackageName());
        mediaPlayerBottom = MediaPlayer.create(context, resID);
        if (mediaPlayerBottom != null) {
            mediaPlayerBottom.start();
            mediaPlayerBottom.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopMusicBottom();
                }
            });
        }
    }

    // 다음 라운드로 넘어갔을때 이전라운드에서 재생 되고 있는 음악을 확실하게 꺼주기 위해서 stop 함수 구현.
    // 다른 버튼 눌렀을때 이전에 나오고 있던 음악 멈추기 위한 용도로 사용
    // 이 부분 없으면 노래가 한번 재생하면 멈추지 않는 좋버그 수시로 발생.
    // 메모리 누수 방지 release() 메서드 호출, 없으면 앱 종료됨
    private void stopMusicTop() {
        if (mediaPlayerTop != null) {
            if (mediaPlayerTop.isPlaying()) {
                mediaPlayerTop.stop();
            }
            mediaPlayerTop.reset();
            mediaPlayerTop.release();
            mediaPlayerTop = null;
        }
    }

    private void stopMusicBottom() {
        if (mediaPlayerBottom != null) {
            if (mediaPlayerBottom.isPlaying()) {
                mediaPlayerBottom.stop();
            }
            mediaPlayerBottom.reset();
            mediaPlayerBottom.release();
            mediaPlayerBottom = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgtop = findViewById(R.id.imageButtonup);
        imgbot = findViewById(R.id.imageButtondown);
        stage = (TextView) findViewById(R.id.Stage);
        textview = (TextView) findViewById(R.id.textView2);
        progressBar = findViewById(R.id.progressBar1);
        ImageButton playButtonTop = findViewById(R.id.playButtonTop);
        ImageButton playButtonBottom = findViewById(R.id.playButtonBottom);

        //위쪽 버튼을 클릭했을때 실행 할 명령, 아래쪽 음악 멈추기, 음악이름 전달해서 해당 노래가 재생되게 함
        playButtonTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 아래쪽 버튼 노래 종료, 이 부분 없으면 아래쪽 노래랑 겹쳐서 들림
                stopMusicBottom();
                // 여기가 노래 선택 핵심
                // res/raw 디렉토리 안에 song_0 부터 song_7까지 노래파일 들어있음, 이미지에 맞게 "song_" 에 "인덱스"를 붙여서 해당 이미지에 맞는 노래 출력
                playMusicTop(getApplicationContext(), "@raw/song_" + window[0]);
            }
        });

        // 아래 쪽 버튼을 눌렀을 때 실행 할 명령, 위와 같은 방식
        playButtonBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopMusicTop(); // 다른 미디어 플레이어 중지
                playMusicBottom(getApplicationContext(), "@raw/song_" + window[1]);
            }
        });

        init(getApplicationContext());

        imgtop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 선택한 번호 큐에 저장.
                queue.offer(window[0]);
                pics_value+=2;
                // 진행도 업데이트 - 선택 한번당 pics_value : 2, pics_value당 6.25
                updateProgressBar((int) ((pics_value+2) * 6.25));

                // 이 부분을 통해 다음 라운드로 넘어갔는지 판단하고, 라운드 넘어갔을때 이전 라운드 노래 종료되게 구현함.
                if (currentRound != pics_value / 2) {
                    currentRound = pics_value / 2;
                    stopMusicTop();
                    stopMusicBottom();
                }

                if(pics_value < 8) { // 처음 8강의 경우! 랜덤 대진표 작성
                    int[] n = getRandomNum();
                    setImages(n[0], n[1], getApplicationContext());
                }

                else if(pics_value == 8){ // 8강 마지막 선택.1:2 2:4 3:6 [4:8]
                    setImages(queue.poll(), queue.poll(), getApplicationContext());
                    stage.setText("4강");
                }
                else if (pics_value < 12){ // 4강, 결승은 여기로 빼서 랜덤 사용 x, 큐 사용
                    setImages(queue.poll(), queue.poll(), getApplicationContext());
                }
                else if(pics_value == 12){ // 4강 마지막 선택
                    stage.setText("결승");
                    setImages(queue.poll(), queue.poll(), getApplicationContext());
                }
                else if (pics_value == 14){ // 결승
                    // 이때 큐에 남는 건 딱 하나 = 결승 우승.
                    stage.setText("우승");
                    int win = queue.poll();
                    setImages(win, win, getApplicationContext());

                    // 나머지 가리기.
                    // 플래이 버튼 가리기 추가
                    imgbot.setVisibility(View.GONE);
                    textview.setVisibility(View.GONE);
                    playButtonTop.setVisibility(View.GONE);
                    playButtonBottom.setVisibility(View.GONE);
                }
            }
        });

        imgbot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queue.offer(window[1]);
                pics_value+=2;

                // 진행도 업데이트 - 선택 한번당 pics_value : 2, pics_value당 6.25
                updateProgressBar((int) ((pics_value+2) * 6.25));

                // 현재 라운드를 카운트 해줌, 만약 라운드가 변했다면 이전 라운드에서 재생중인 노래 싹다 정지
                if (currentRound != pics_value / 2) {
                    currentRound = pics_value / 2;
                    stopMusicTop();
                    stopMusicBottom();
                }

                if(pics_value < 8) { // 처음 8강의 경우! 랜덤 대진표 작성
                    int[] n = getRandomNum();
                    setImages(n[0], n[1], getApplicationContext());
                }

                else if(pics_value == 8){ // 8강 마지막 선택.1:2 2:4 3:6 [4:8]
                    setImages(queue.poll(), queue.poll(), getApplicationContext());
                    stage.setText("4강");
                }
                else if (pics_value < 12){ // 4강, 결승은 여기로 빼서 랜덤 사용 x, 큐 사용
                    setImages(queue.poll(), queue.poll(), getApplicationContext());
                }
                else if(pics_value == 12){ // 4강 마지막 선택
                    stage.setText("결승");
                    setImages(queue.poll(), queue.poll(), getApplicationContext());
                }
                else if (pics_value == 14) { // 결승
                    // 이때 큐에 남는 건 딱 하나 = 결승 우승.
                    stage.setText("우승");
                    int win = queue.poll();
                    setImages(win, win, getApplicationContext());

                    // 나머지 가리기.
                    // 재생 버튼 가리기 추가
                    imgbot.setVisibility(View.GONE);
                    textview.setVisibility(View.GONE);
                    playButtonTop.setVisibility(View.GONE);
                    playButtonBottom.setVisibility(View.GONE);
                }
            }
        });
    }

    public static void init(Context context){ // 초기값 지정.
        // stage - textview 8강으로 초기화
        stage.setText("8강");

        // 8강 배열 전부 false로 초기화
        for(int i = 0; i < pics.length;i++){
            pics[i] = false;
        }
        // 맨처음 사진 결정
        int[] n = getRandomNum();
        setImages(n[0], n[1], context);
        window = new int[]{n[0], n[1]};

        updateProgressBar((int) 12.5);
    }

    public static void updateProgressBar(int progress) {
        progressBar.setProgress(progress);
    }

    public static void setImages(int n1, int n2, Context context){
        // 리소스를 숫자를 통해 접근하는 방법.
        int drawble1 = context.getResources().getIdentifier("@drawable/busker_" + n1,
                "drawable", context.getPackageName());
        int drawble2 = context.getResources().getIdentifier("@drawable/busker_" + n2,
                "drawable", context.getPackageName());
        // 접근해서 사진 변경.
        imgtop.setImageResource(drawble1);
        imgbot.setImageResource(drawble2);
        // 변경된 사진 window에 저장.
        window = new int[]{n1, n2};
    }


    public static int[] getRandomNum(){ // 기존 배열 값 고려한 2개 숫자 랜덤 발생 함수.
        while (true){
            int n1 = (int)(Math.random() * 8); //0~7 범위 숫자 랜덤 생성
            int n2 = (int)(Math.random() * 8);
            if((n1 != n2) && !pics[n1] && !pics[n2]){
                pics[n1] = pics[n2] = true;
                return new int[] {n1,n2};
            }
        }

    }
}