package com.example.hwi.pokertower;

import java.util.Random;

public class pokerSet {

    public int[] pokerdraw() {
        // ---------------------------------------------------------
        // 카드 모양과 숫자를 입력받기위한 함수 및 변수선언
        // ---------------------------------------------------------
        String[] suit = new String[10];
        int[] gamer = new int[10];
        int[] result = new int[8]; //앞에 7개 카드이름 뒤에 하나 결과값
        String number = "A23456789TJQK";

        int i, j, k, temp, count = 0, flush = 0, straight = 0;
        String temp_s;
        int key = 0;
        // ---------------------------------------------------------
        // 카드 게임을 계속하기위한 do-while 게임값의 초기화 필요
        // ---------------------------------------------------------
        count = flush = straight = 0;

        // -------------------52장 카드한벌을 만든다-------------------
        Random card = new Random();
        do {
            k = 0;
            for (i = 0; i < 7; i++)
                gamer[i] = (int) (Math.random() * 52)+1; // 1~52 랜덤

            for (i = 0; i < 7; i++) {
                for (j = i; j < 7; j++) {

                    // -------------------서로 다른카드가 나올때까지
                    // 반복한다-------------------
                    if (gamer[i] == gamer[j + 1])
                        k = 1;
                }
            }
        } while (k == 1);

        //카드 값 7장 넘겨주기
        for(int a=0; a<8; a++)
        {
            result[a] = gamer[a];
        }

        // -------------------5개의 카드의 모양과 숫자를 지정한다-------------------
        for (i = 0; i < 7; i++) {
            if (gamer[i] >= 1 && gamer[i] <= 13) {
                suit[i] = "spade";
            } else if (gamer[i] >= 14 && gamer[i] <= 26) {
                suit[i] = "diamond";
                gamer[i] = gamer[i] - 13;
            } else if (gamer[i] >= 27 && gamer[i] <= 39) {
                suit[i] = "heart";
                gamer[i] = gamer[i] - 26;
            } else if (gamer[i] >= 40 && gamer[i] <= 52) {
                suit[i] = "club";
                gamer[i] = gamer[i] - 39;
            }
        }

        // -------------------받은 카드를 카드 숫자크기로 정렬한다-------------------
        for (i = 0; i < 7; i++) {
            for (j = 0; j < 6; j++) {
                if (gamer[j] > gamer[j + 1]) {
                    temp = gamer[j];
                    gamer[j] = gamer[j + 1];
                    gamer[j + 1] = temp;

                    temp_s = suit[j];
                    suit[j] = suit[j + 1];
                    suit[j + 1] = temp_s;
                }
            }
        }
        // -------------------패어값 계산하는 순환문-------------------
        for (i = 0; i < 6; i++) {
            for (j = i; j < 6; j++) {
                if (gamer[i] == gamer[j + 1]) {
                    count++;
                }
            }
        }
        // -------------------플러시의 비교연산-------------------
        int spadecount = 0;
        int clubcount = 0;
        int heartcount = 0;
        int diamondcount = 0;
        for (int d = 0; d < 7; d++) {
            if (suit[d].equals("spade"))
                spadecount++;
            if (suit[d].equals("heart"))
                heartcount++;
            if (suit[d].equals("diamond"))
                diamondcount++;
            if (suit[d].equals("club"))
                clubcount++;
            if (spadecount > 4 || heartcount > 4 || diamondcount > 4 || clubcount > 4)
                flush++;
        }
        // -------------------스트레이트의 비교연산-------------------
        if (gamer[0] + 1 == gamer[1] && gamer[1] + 1 == gamer[2] && gamer[2] + 1 == gamer[3]
                && gamer[3] + 1 == gamer[4])
            straight++;

        // 로얄스트레이트 플러쉬 = 10 스트레이트 플러쉬 = 9 포카드 = 8 풀하우스 = 7
        // 플러쉬 = 6 스트레이트 = 5 트리플 = 4 투페어 = 3 원페어 = 2 노페어 = 1
        // -------------------족보 결과를 출력한다-------------------
        if (gamer[0] == 1 && gamer[1] == 10 && gamer[2] == 11 && gamer[3] == 12 && gamer[4] == 13 && suit[0] == "spade"
                && flush == 1) // 로얄 스트레이트 플러쉬
        {
            result[7] =  10;
        } else if (flush >= 1 && straight == 1) // 스트레이트 플러쉬
        {
            result[7] = 9;
        } else if (count == 5) // 포카드
        {
            result[7] = 8;
        } else if (count == 4) // 풀하우스
        {
            result[7] = 7;
        } else if (flush >= 1) // 플러쉬
        {
            result[7] = 6;
        } else if (straight == 1) // 스트레이트
        {
            result[7] = 5;
        } else if (count == 3) // 트리플
        {
            result[7] = 4;
        } else if (count == 2) // 투 페어
        {
            result[7] = 3;
        } else if (count == 1) // 원 페어
        {
            result[7] = 2;
        } else {
            result[7] = 1;
        }

        return result;
    }
}