package com.example.hwi.logintest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;



public class login extends Activity {
    EditText edit_id, edit_pw;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         edit_id = (EditText) findViewById(R.id.login_id);
         edit_pw = (EditText) findViewById(R.id.login_pw);//edit_pw변수와 xml에 pw입력 부분을 연결합니다.
    }

    public void login_btn(View view) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
//안드로이드 정책이 변경되어서 이제 메인 클래스? 함수?에서는 통신을 할 수 없습니다.
//그래서 많은 분들이 따로 클래스를 정의하고 AsyncTask를 사용하지만 저는 자꾸 오류가 나서 이 방법을 사용하였습니다.
//위에 두줄을 추가해주시면 굳이 클래스를 빼지 않아도 http 통신을 하실 수 있습니다.
        String sMessage = edit_id.getText().toString();

        String result = SendByHttp(sMessage);
//SendByHttp함수 실행시, 데이터를 보내고, 그에따른 데이터를 받아와서 result에 저장합니다.
        String[][] parsedData = jsonParserList(result);
//받아온 형태가 json이므로 파싱합니다. jsonParserList함수는 받아온 데이터를 파싱하는 함수입니다.
//json을 이용하는 이유.
//제가 생각하는 json이용하는 이유인데 이게 맞는지 모르겠습니다. 틀릴 시 댓글부탁드립니다.^^ 저 초보자라서요 ㅎㅎ
//보통에 경우 xml이나 jsp를 가져온다는 뜻은 jsp 파일에 처음부터 끝,그러니깐 <head>부터 시작해서 그냥 쭈우욱 텍스트로 가져옵  니다.
//이럴경우에는 내가 원하는 텍스트만 뽑아오기 힘들겠죠?
//그래서 json을 사용할 경우에는 내가 원하는 부분만 데이테명:데이터내용 이런식으로 가져오는 것입니다.
//ex){"List":[{"data1":"sfasf".""data2:"sdfsdf"}]} 이런식으로 원하는 데이터값만 가져옵니다.
//그래서 많이 간단해진 이 텍스트를 뭐는=뭐다 이렇게 더 간단하게 안드로이드에서 작업하는 과정을 파싱한다고 합니다.~
        if(parsedData[0][0].equals("succed"))
        {
//이 부분은 제가 아이디랑 비밀번호를 get방식으로 보냈죠? 그 데이터가 웹으로 날아가서 db 내용들과 비교한 뒤,
//올바른 경우에는 웹에서 json형태로 첫번째 배열에 succed를 넣었습니다.아닌경우에는 failed를 넣었고요.
//그걸 비교해서 succed일 경우에는 로그인 성공 매세지를 띄우고(메인엑티비티일 경우 그냥 this로 쓰세요~),
//메인 클래스로 인텐트 하는 부분입니다.
            Toast.makeText(login.this, "로그인 성공", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(login.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    private String SendByHttp(String msg) {
        if (msg == null) {
            msg = "";
        }

        String URL = "http://localhost:8080/JSP_HWI/test.jsp"; //자신의 웹서버 주소를 저장합니다.
        DefaultHttpClient client = new DefaultHttpClient();//HttpClient 통신을 합니다.
//Http 라이브러리가 필요합니다.
        try {
            HttpPost post = new HttpPost(URL + "?id=" + edit_id.getText().toString()+"&pw="+edit_pw.getText().toString());
//웹서버로 데이터를 전송합니다. 요번에 경우에는 get방식으로 데이터를 전송하였습니다.
//간단히 설명하면 주소?데이터명=데이터내용&데이터명=데이터내용   이런식입니다.
            HttpResponse response = client.execute(post);
//데이터를 보내고 바로 데이터 응답을 받습니다.
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
//받아온 데이터를 buffer에 넣습니다.
            String line = null;
            String result = "";

            while ((line = bufreader.readLine()) != null) {
                result += line;
            }
//buffer를 읽어와서 result에 넣습니다.
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            client.getConnectionManager().shutdown();
//예외처리입니다.
            return "";
        }
    }
    //받아온 데이터를 파싱하는 부분입니다.
    public String[][] jsonParserList(String pRecvServerPage){
        Log.i("서버에서 받은 전체 내용", pRecvServerPage);
//받아온 데이터를 확인합니다. 이 부분을 확인하고 싶으시면, 안드로이드 하단에 쭉쭉 뭐 뜨는거 보이시면 그거 Log들인데 거기게 흔적을 남기는 겁니다. info에서 찾아보시면 찾으실 수 있습니다.
        try{
//아까말한 {"List":[{"data1":"sfasf".""data2:"sdfsdf"}]} 이형태를 분해하는 과정입니다.
//저도 잘 몰라요..
//아래 어떤 대단한 분에 코딩을 참고하였습니다. 이 사이트에서 자세한 내용을 확인해보세요.^^
            JSONObject json = new JSONObject(pRecvServerPage);
            JSONArray jArr = json.getJSONArray("List");

            String[] jsonName = {"msg1","msg2","msg3"};
            String[][] parseredData = new String[jArr.length()][jsonName.length];
            for(int i = 0; i<jArr.length();i++){
                json = jArr.getJSONObject(i);
                for (int j=0;j<jsonName.length; j++){
                    parseredData[i][j] = json.getString(jsonName[j]);
                }

            }
//어떤식으로 분해하였는지 또 Log를 찍어 알아봅니다. 굳이 안 넣으셔도 됩니다.
            for(int i=0;i<parseredData.length;i++)
            {
                Log.i("JSON을 분석한 데이터"+i+":",parseredData[i][0]);
                Log.i("JSON을 분석한 데이터"+i+":",parseredData[i][1]);
                Log.i("JSON을 분석한 데이터"+i+":",parseredData[i][2]);
            }


            return parseredData;
//잘 파싱된 데이터를 넘깁니다.
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }
}

