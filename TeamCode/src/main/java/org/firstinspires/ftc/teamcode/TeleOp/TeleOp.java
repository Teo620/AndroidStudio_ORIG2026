package org.firstinspires.ftc.teamcode.TeleOp;

import static org.firstinspires.ftc.teamcode.pedroPathing.Tuning.follower;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.geometry.Pose;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.SubSistems.Formula;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Config
@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name="TeleOp", group="Linear OpMode")
public class TeleOp extends OpMode {
   private Formula formula = new Formula(); //Cele mai bune formule

    public FtcDashboard dashboard;




    private IMU imu;
    public DcMotor LFMotor = null, LBMotor = null, RFMotor=null, RBMotor=null;
    public DcMotor Intake = null;
    public Servo ServoRotireComb = null;
    private Limelight3A limelight;
    int TagID;

    boolean InvertControl = false;
    @Override
    public void init(){

        LFMotor = hardwareMap.get(DcMotor.class, "lf");
        LBMotor = hardwareMap.get(DcMotor.class, "lb");
        RFMotor = hardwareMap.get(DcMotor.class, "rf");
        RBMotor = hardwareMap.get(DcMotor.class, "rb");
        LFMotor.setDirection(DcMotor.Direction.REVERSE);
        LBMotor.setDirection(DcMotor.Direction.REVERSE);

        Intake = hardwareMap.get(DcMotor.class ,"intake");

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        imu = hardwareMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot revHubOrientationOnRobot = new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                RevHubOrientationOnRobot.UsbFacingDirection.UP);
        imu.initialize(new IMU.Parameters(revHubOrientationOnRobot));
        limelight.start();
        limelight.pipelineSwitch(0);

      //  follower= Constants.createFollower(hardwareMap);
      //  follower.setStartingPose(new Pose(20 ,123, Math.toRadians(140)));
      //  follower.update();


    }



    public void start()
    {

    }

    public void loop(){

        if(gamepad1.squareWasPressed())
            InvertControl=!InvertControl;

        formula.driveJoystick(InvertControl);

        if(gamepad1.right_trigger > 0)
            Intake.setPower(0.5);
        else if(gamepad1.left_trigger > 0)
            Intake.setPower(-0.5);
        else Intake.setPower(0);

    }


}
