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



@Component // stringのComponentScanで引っ掛ける
public class SlackBot extends Bot {

    private static final Logger logger = LoggerFactory.getLogger(SlackBot.class);

    // botのAPITokenの取得
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

    // ダイレクトメッセージとダイレクトメッセージが来たらメッセージを返す
    @Controller(events = {EventType.DIRECT_MENTION, EventType.DIRECT_MESSAGE})
    public void onReceiveDM(WebSocketSession session, Event event) {
        reply(session, event, new Message("こんにちは、私は" + slackService.getCurrentUser().getName() + "だよ!"));
    }


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



    // 会話始めるためのメソッド
    @Controller( pattern = ("今日のエスカフェ"), next = "confirmTiming")
    public void setupMeeting(WebSocketSession session, Event event) {
        if (week() == "月") {
            startConversation(event, "confirmTiming");   // start conversation
            reply(session, event, new Message("今日も１７時からあるよ！行けますか？"));
        }else{
            reply(session, event, new Message("今日はSCafeはないよ"));
            stopConversation(event);
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
            reply(session, event, new Message("では、何時集合がいいですか？"));
            nextConversation(event); // stop conversation
        }
    }

    @Controller
    public void askSomeone(WebSocketSession session, Event event) {
        if (event.getText().contains("はい")) {
            reply(session, event, new Message("では、運転手になってくれる人、今週もよろしくおねがいします！今日も頑張りましょう！！"));
        } else if(event.getText().contains("了解")){
            reply(session, event, new Message("では、運転手になってくれる人、今週もよろしくおねがいします！今日も頑張りましょう！！"));
        }else if(event.getText().contains("了解")){
            reply(session, event, new Message("かしこまりました。“ + event.getText() + “という意見が出ていますが皆さんどうですか？よろしければその時間に集合ということでよろしくお願いします。"));
        }
        stopConversation(event);    // stop conversation
    }


    @Controller(pattern = "(おみくじ)")
    public void omikuji(WebSocketSession session, Event event) { // created by Uehara Kazuma.(e175740)
        int i ;
        String result;
        i = (int)(Math.random()*100)+1;
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
}