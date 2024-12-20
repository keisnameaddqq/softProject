package Communication;

import java.io.Serializable;

/**
 * Problem:
 * Author:
 * Date:2023/11/2816:26
 **/
public class MyMessage implements Serializable {

    public int MessageAbout=-1;//-1返回的结果、0请求AboutLogin、1请求AfterLoginFace、2请求Game1
    public final static int RetToCustomer=-1,AskForLogin=0,AskForLoginFace=1,AskForGame2=2;

    public AboutLoginMessage aboutLoginMessage;

    public AfterLoginFaceMessage afterLoginFaceMessage;

    public Game2Message game2Message;


    public MyMessage(){
        aboutLoginMessage=new AboutLoginMessage();
        afterLoginFaceMessage=new AfterLoginFaceMessage();
        game2Message =new Game2Message();
    }

    public MyMessage(AboutLoginMessage aboutLoginMessage ) {
        this.aboutLoginMessage=new AboutLoginMessage(aboutLoginMessage);
        afterLoginFaceMessage=new AfterLoginFaceMessage();
        game2Message =new Game2Message();
    }


    public MyMessage(AfterLoginFaceMessage afterLoginFaceMessage){
        aboutLoginMessage=new AboutLoginMessage();
        this.afterLoginFaceMessage=new AfterLoginFaceMessage(afterLoginFaceMessage);
        game2Message =new Game2Message();
    }

    public MyMessage(Game2Message game2Message){
        aboutLoginMessage=new AboutLoginMessage();
        this.afterLoginFaceMessage=new AfterLoginFaceMessage();
        this.game2Message = new Game2Message(game2Message);
    }

}

