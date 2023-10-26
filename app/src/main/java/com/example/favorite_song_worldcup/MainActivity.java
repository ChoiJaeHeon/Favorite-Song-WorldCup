package com.example.favorite_song_worldcup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.LinkedList;
import java.util.Queue;

public class MainActivity extends AppCompatActivity {

    public static ImageButton imgtop;
    public static ImageButton imgbot;
    public static TextView stage;
    public static TextView textview;
    public static ProgressBar progressBar;
    // 8강 생성시 랜덤생성 위한, 배열

    // 8강,4강,결승에서 선택한 값들을 순서대로 저장하기 위한 큐.
    Queue<Integer> queue = new LinkedList<>();
    public static boolean[] pics = new boolean[16];
    // 현재 선택할 수 있는 index 2개 static으로 저장.
    static int[] window= new int[2];

    // 선택할 때마다, 2씩 더해서 현재 위치 추적. 0~7 -> 8강, 8~11 : 4강, 12~ 결승
    static int pics_value = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgtop = findViewById(R.id.imageButtonup);
        imgbot = findViewById(R.id.imageButtondown);
        stage = (TextView) findViewById(R.id.Stage);
        textview = (TextView) findViewById(R.id.textView2);
        progressBar = findViewById(R.id.progressBar1);



        init(getApplicationContext());

        imgtop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 선택한 번호 큐에 저장.
                queue.offer(window[0]);
                pics_value+=2;
                // 진행도 업데이트 - 선택 한번당 pics_value : 2, pics_value당 6.25
                updateProgressBar((int) ((pics_value+2) * 6.25));

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
                    imgbot.setVisibility(View.GONE);
                    textview.setVisibility(View.GONE);
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