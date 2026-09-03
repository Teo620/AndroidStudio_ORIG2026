package org.firstinspires.ftc.teamcode.TeleOp;

import static androidx.core.math.MathUtils.clamp;
import static org.firstinspires.ftc.teamcode.pedroPathing.Tuning.follower;

import static java.lang.Math.abs;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.geometry.Pose;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.arcrobotics.ftclib.controller.PIDFController;

import org.firstinspires.ftc.teamcode.SubSistems.Formula;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Config
@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name="TeleOp", group="Linear OpMode")
public class TeleOp extends OpMode {
   private Formula formula = new Formula(); //Cele mai bune formule

    public FtcDashboard dashboard;


    private PIDFController pidf;

    private IMU imu;
    public DcMotor LFMotor = null, LBMotor = null, RFMotor=null, RBMotor=null;
    public DcMotor Intake = null, RGlis = null, LGlis = null;

    public Servo Clamp = null, Servo_Polen = null;
    public Servo ServoRotireComb = null;
    private Limelight3A limelight;
    int TagID;
    float Target_Pos = 0, GSpeed = 1;

    public static double p = 0.0008, i = 0, d = 0.0002 , f = 0.12;

    boolean InvertControl = false;
    @Override
    public void init(){
/*
        LFMotor = hardwareMap.get(DcMotor.class, "lf");
        LBMotor = hardwareMap.get(DcMotor.class, "lb");
        RFMotor = hardwareMap.get(DcMotor.class, "rf");
        RBMotor = hardwareMap.get(DcMotor.class, "rb");
        LFMotor.setDirection(DcMotor.Direction.REVERSE);
        LBMotor.setDirection(DcMotor.Direction.REVERSE);
*/
        pidf = new PIDFController(p, i, d, f);
        Intake = hardwareMap.get(DcMotor.class ,"intake");
        Clamp = hardwareMap.get(Servo.class ,"clamp");
        Servo_Polen = hardwareMap.get(Servo.class ,"polen");
        LGlis= hardwareMap.get(DcMotor.class, "lg");
        RGlis = hardwareMap.get(DcMotor.class, "rg");
        LGlis.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        RGlis.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LGlis.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        RGlis.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        RGlis.setDirection(DcMotor.Direction.REVERSE);

/*
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        imu = hardwareMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot revHubOrientationOnRobot = new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                RevHubOrientationOnRobot.UsbFacingDirection.UP);
        imu.initialize(new IMU.Parameters(revHubOrientationOnRobot));
        limelight.start();
        limelight.pipelineSwitch(0);
*/
    }



    public void start()
    {

    }

    public void loop(){

        pidf.setPIDF(p, i, d, f);

        if(gamepad1.squareWasPressed())
            InvertControl=!InvertControl;

        //formula.driveJoystick(InvertControl);

        if(gamepad1.right_trigger > 0)
            Intake.setPower(1);
        else if(gamepad1.left_trigger > 0)
            Intake.setPower(-1);
        else Intake.setPower(0);

        if(gamepad1.cross)
            Clamp.setPosition(0.50); //DESCHIS
        else if(gamepad1.triangle)
            Clamp.setPosition(0.3); //INCHIS


         if(abs(gamepad1.right_stick_y)>0.05)
        Target_Pos += -gamepad1.right_stick_y * GSpeed;

        if(gamepad1.dpad_left)
            Servo_Polen.setPosition(0);
        else if(gamepad1.dpad_right)
            Servo_Polen.setPosition(0.67);

        Target_Pos = clamp(Target_Pos,0,67);
        int Current_Pos = LGlis.getCurrentPosition();
        double power = pidf.calculate(Current_Pos, Target_Pos);
        power=clamp(power,0,0.3);
        LGlis.setPower(power);
        RGlis.setPower(power);


    }


}
