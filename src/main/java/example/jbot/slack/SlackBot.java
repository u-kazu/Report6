package example.jbot.slack;

import me.ramswaroop.jbot.core.slack.Bot;
import me.ramswaroop.jbot.core.slack.Controller;
import me.ramswaroop.jbot.core.slack.EventType;
import me.ramswaroop.jbot.core.slack.models.Event;
import me.ramswaroop.jbot.core.slack.models.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import java.util.Calendar;

/*
SlackBotの機能としては、会話、ミニゲームの主に2つである
会話は、DIRECT_MESSAGEとメンション、ある言葉に反応するように実装
ミニゲームは、おみくじとじゃんけんのミニゲームを搭載した。
 */

@Component // stringのComponentScanで引っ掛ける
public class SlackBot extends Bot {

    private static final Logger logger = LoggerFactory.getLogger(SlackBot.class);

    /* botのAPITokenの取得
    Tokenは第三者に知られたくないのカプセル化の実装が不可欠である
     */
    @Value("${slackBotToken}")// @valueは、キーがない時のデフォルトの値を設定する。@Value("${キー名:デフォルト値}")
    private String slackToken;

    @Override
    public String getSlackToken() {
        return slackToken;
    }

    @Override
    public Bot getSlackBot() {
        return this;
    }

    /* ダイレクトメッセージとダイレクトメッセージが来たらメッセージを返す
    DIRECT_MENTIONは、メンション(@username)のリプに反応する
    DIRECT_MESSAGEは、個人のやりとりに反応する
    onReceiveDMでは、Botが反応するかどうかを確認するための試験的なメソッド
     */

    @Controller(events = {EventType.DIRECT_MENTION, EventType.DIRECT_MESSAGE})
    public void onReceiveDM(WebSocketSession session, Event event) {
        reply(session, event, new Message("こんにちは、私は" + slackService.getCurrentUser().getName() + "だよ!"));
    }

    /*
    week()は、曜日を返すメソッド
    7つの配列を作りそれぞれ曜日を入れる
    Calendar.getInstance();によってカレンダーの取得
     */

    public String week(){
        Calendar now = Calendar.getInstance();

        String[] week = new String[7];
        week[0] = "日";
        week[1] = "月";
        week[2] = "火";
        week[3] = "水";
        week[4] = "木";
        week[5] = "金";
        week[6] = "土";

        int this_week = now.get(now.DAY_OF_WEEK);
        String d = week[this_week - 1];
        return d;
    }

    /* コメント 作成者 新城 巧也
    会話を行うメソッド
    patternに入力された言葉に反応する。また、nextによって次のメソッドに移る(遷移)
    startConversationで会話の開始
    nextConversation(event);によって次のメソッドに移される
    stopConversation(event);によって会話を止める
     */

    @Controller( pattern = ("今日のエスカフェ"), next = "confirmTiming") // 次の移動先をconfirmTimingに指定
    public void setupMeeting(WebSocketSession session, Event event) {
        if (week() == "水") { // もし水曜日なら次のメソッドに移り、それ以外の曜日なら会話を終わる
            startConversation(event, "confirmTiming");   // start conversation
            reply(session, event, new Message("今日も１７時からあるよ！行けますか？")); // メッセージの出力
            nextConversation(event);
        }else{
            reply(session, event, new Message("今日はSCafeはないよ"));
            stopConversation(event); // stop conversation
        }
    }



    @Controller(next = "askWhetherToRepeat") // 次のメソッド指定(遷移)
    public void confirmTiming(WebSocketSession session, Event event) {
        if (event.getText().contains("はい")) {
            reply(session, event, new Message("事務室前のロビーに１６時４５分集合でいいですか？"));
            nextConversation(event);
        }else if(event.getText().contains("うん")) {
            reply(session, event, new Message("事務室前のロビーに１６時４５分集合でいいですか？"));
            nextConversation(event);
        }else{
            reply(session, event, new Message("そっかーじゃあまた来週！ばいば〜い。"));
            stopConversation(event);
        }
    }


    @Controller(next = "askSomeone")
    public void askWhetherToRepeat(WebSocketSession session, Event event) {
        if (event.getText().contains("はい")) {
            reply(session, event, new Message("では、時間厳守でよろしくお願いします。"));
            nextConversation(event);
        }else if (event.getText().contains("了解")) {
            reply(session, event, new Message("では、時間厳守でよろしくお願いします。"));
            nextConversation(event);
        }else{
            reply(session, event, new Message("時間の折り合いは各自で決めてください"));
            stopConversation(event);
        }
    }

    @Controller
    public void askSomeone(WebSocketSession session, Event event) {
        if (event.getText().contains("はい")) {
            reply(session, event, new Message("では、運転手になってくれる人、今週もよろしくおねがいします！今日も頑張りましょう！！"));
        } else if (event.getText().contains("了解")) {
            reply(session, event, new Message("では、運転手になってくれる人、今週もよろしくおねがいします！今日も頑張りましょう！！"));
            stopConversation(event);    // stop conversation
        }
    }


    @Controller(pattern = "(おみくじ)")
    public void omikuji(WebSocketSession session, Event event) { // おみくじを引くメソッド。
        int i ;
        String result;
        i = (int)(Math.random()*100)+1;  // 1~100の乱数を生成。
        System.out.println(i);
        if (i <= 10) {
            result = "大吉";
        } else if (i <= 30) {
            result = "吉";
        } else if (i <= 50) {
            result = "中吉";
        } else if (i <= 70) {
            result = "小吉";
        } else if (i <= 90) {
            result = "凶";
        } else {
            result = "大凶";
        }
        reply(session, event, new Message("おみくじの結果は「" + result + "」でした！"));
    }




    @Controller(pattern = "(彼女欲しい)")
    public void girlFriend(WebSocketSession session, Event event) { // 適当な会話。
        reply(session,event, new Message("水35リットル,炭素20kg,アンモニア４リットル,石灰1.5kg,リン800ｇ,塩分250ｇ,硝石100ｇ,硫黄80ｇ,フッ素7.5ｇ,鉄５ｇ,ケイ素３ｇ,その他少量の15の元素,があれば作れるそうですよ。"));
    }

    @Controller(pattern = "(じゃんけん)", next = "(janken2)")
    public void janken(WebSocketSession session, Event event) { // chatbotとじゃんけんをするメソッド。
        reply(session,event,new Message("じゃんけんを始めます。グー、チョキ、パー、のうちどれかを出してください。\nじゃんけん..."));
        nextConversation(event); // ユーザ入力を受け取るため、次のメソッドへ続く。
    }

    @Controller
    public void janken2(WebSocketSession session, Event event) { //じゃんけんメソッドその2
        int i ;
        String hand ;
        i = (int)(Math.random()*3)+1; // 1~3の乱数で手を決める。
        if (i == 1) {
            hand = "グー";
        } else if (i == 2) {
            hand = "チョキ";
        } else {
            hand = "パー";
        }
        reply(session,event,new Message("ポン！私の手は「" + hand + "」です。")); // 勝敗判定部分
        if ((event.getText() == "グー" && hand == "チョキ") || (event.getText() == "チョキ" && hand == "パー") || (event.getText() == "パー" && hand == "グー")) {
            reply(session,event,new Message("あなたの勝ちです！"));
        } else if ((event.getText() == "グー" && hand == "パー") || (event.getText() == "チョキ" && hand == "グー") ||(event.getText() == "パー" && hand == "チョキ")) {
            reply(session,event,new Message("あなたの負けです！"));
        } else {
            reply(session,event,new Message("あいこでした。"));
        }
    }
}