package org.firstinspires.ftc.teamcode.Auto;

import static androidx.core.math.MathUtils.clamp;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.controller.PIDFController;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.SubSistems.Formula;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import java.util.List;import com.arcrobotics.ftclib.controller.PIDFController;

@Config
@Autonomous(name="Auto_Rosu", group="Linear OpMode")
public class Auto_Rosu extends OpMode {

    private Follower follower;
    private Formula formula = new Formula();

    private Timer pathTimer, opModeTimer,aux,shootTimer,timp_pentru_tras_minge,auxTime,timp_pentru_aruncat_minge;

    public DcMotorEx TURELA = null;
    public DcMotor LIFT = null;
    public DcMotor INTAKE=null;
    public Servo Clamp = null, Servo_Polen = null, Clamp_Angle = null;
    private final Pose startPose = new Pose(180,12,Math.toRadians(0));
    private final Pose Human =new Pose(40,36,Math.toRadians(0));
    private final Pose Polen =new Pose(228,36,Math.toRadians(0));
    private final Pose[] Parkings ={
            new Pose(167,60,Math.toRadians(0)),  //case 1
            new Pose(142,60,Math.toRadians(0)),  //case 2
            new Pose(70,60,Math.toRadians(0))}; //case 3
    private Pose Final_Park=new Pose(0,(0),Math.toRadians(0));

    public double start_polen = 0, start_angle = 0.4, start_clamp = 0.5;
    Limelight3A limelight;
    IMU imu;
    public static double p = 0.005, i = 0, d = 0.0003, f = 0.05;
    float Target_Pos = 0, GSpeed = 1;
    public DcMotor Intake = null, RGlis = null, LGlis = null;
    int TagID;
    int facut=0, iteration=0;

    public enum PathState{
        startpos_Polen, Polen_Stem, Stem_Polen2, go_Stem, go_Park, stop;
    }
    private PIDFController pidf;
    private final Pose Comb =new Pose(180,36,Math.toRadians(-90));
    PathState pathState;
    Pose lastPose;

    public void buildPaths(){}

    public PathChain Drive(Pose a,Pose b){
        return follower.pathBuilder()
                .addPath(new BezierLine(a,b))
                .setLinearHeadingInterpolation(a.getHeading(), b.getHeading())
                .build();
    }


    public void startPathUpdate(){
        switch (pathState) {

            case startpos_Polen:
                if(facut==0){

                    INTAKE.setPower(0.7);
                    follower.followPath(Drive(startPose,Polen), true);
                    facut=1;
                }
                if(!follower.isBusy()) {
                    setPathState(PathState.go_Park);
                }
                break;

            case go_Stem:
                if(facut==0){

                    follower.followPath(Drive(follower.getPose(),Human), true);
                    INTAKE.setPower(0);
                    LGlis.setTargetPosition(200);
                    RGlis.setTargetPosition(200);
                    Servo_Polen.setPosition(0.45);

                    if(pathTimer.getElapsedTimeSeconds() < 3)
                        break;

                    Clamp_Angle.setPosition(0.75);

                    if(pathTimer.getElapsedTimeSeconds() <= 6)
                        break;
                    Clamp.setPosition(0.7);

                    if(pathTimer.getElapsedTimeSeconds() <= 9)
                        break;

                    facut=1;


                }
                if(!follower.isBusy()) {
                    if(iteration==0)
                        setPathState(PathState.go_Park);
                }
                break;

            case go_Park:
                if(facut==0){
                    follower.followPath(Drive(follower.getPose(),Final_Park), true);
                    facut=1;
                }
                if(!follower.isBusy()) {
                    if(iteration==0)
                        setPathState(PathState.stop);
                }
                break;

            case stop:

                LGlis.setTargetPosition(0);
                RGlis.setTargetPosition(0);
                INTAKE.setPower(0);
                break;

            default:    break;
        }
    }
    public void setPathState(PathState newState){

        pathState=newState;
        pathTimer.resetTimer();
        facut=0;
    }



    @Override
    public void init(){
        LGlis = hardwareMap.get(DcMotor.class, "lg");
        RGlis = hardwareMap.get(DcMotor.class, "rg");
        LGlis.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        RGlis.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LGlis.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        RGlis.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        RGlis.setDirection(DcMotor.Direction.REVERSE);
        INTAKE =hardwareMap.get(DcMotor.class,"intake");
        Clamp = hardwareMap.get(Servo.class, "clamp");
        Servo_Polen = hardwareMap.get(Servo.class, "polen");
        Clamp_Angle = hardwareMap.get(Servo.class, "angle");
        imu = hardwareMap.get(IMU.class, "imu");
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        RevHubOrientationOnRobot revHubOrientationOnRobot = new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                RevHubOrientationOnRobot.UsbFacingDirection.UP);
        imu.initialize(new IMU.Parameters(revHubOrientationOnRobot));
        limelight.pipelineSwitch(0);
        limelight.start();

        pathState= PathState.startpos_Polen;
        pathTimer=new Timer();
        opModeTimer=new Timer();
        timp_pentru_tras_minge=new Timer();
        auxTime=new Timer();
        follower= Constants.createFollower(hardwareMap);
        buildPaths();
        follower.setPose(startPose);

        Clamp.setPosition(start_clamp);
        Clamp_Angle.setPosition(start_angle);
        Servo_Polen.setPosition(start_polen);
        pidf = new PIDFController(p, i, d, f);
    }

    public void init_loop()
    {
        //limelight.updateRobotOrientation(Math.toDegrees(follower.getPose().getHeading()));
        LLResult result = limelight.getLatestResult();

        if (result != null && result.isValid()) {
            List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();

            if (!fiducials.isEmpty()) {
                // Lock in the ID to your variable
                TagID = fiducials.get(0).getFiducialId();
            }
        }

        int Case = 0;

        if(TagID==21){
            Case=0;
        }else if(TagID==22){
            Case=1;
        }else if(TagID==23){
            Case=2;
        }
        telemetry.addData("ID OBELISK",Case);
        telemetry.addData("Camera",result);
        telemetry.update();
    }

    public void start(){
        opModeTimer.resetTimer();
        setPathState(pathState);

        int Case = -1;

        if(TagID==21){
            Case=0;
        }else if(TagID==22){
            Case=1;
        }else{
            Case=2;
        }

        Final_Park=Parkings[Case];
        telemetry.addData("Xpark",Final_Park.getX());
    }

    @Override
    public void loop(){
        // PIDF();
        follower.update();
        startPathUpdate();
        telemetry.addData("Xpark",Final_Park.getX());
        telemetry.addData("ID OBELISK",TagID);
        telemetry.addData("FACEM LOOP",1);
        telemetry.addData("X",follower.getPose().getX());
        telemetry.addData("Y",follower.getPose().getY());
        telemetry.update();


    }

    public void PIDF(){
        pidf.setPIDF(p, i, d, f);
        Target_Pos = clamp(Target_Pos, 0, 150);
        int Current_Pos = LGlis.getCurrentPosition();
        double power = pidf.calculate(Current_Pos, Target_Pos);
        power = clamp(power, -0.4, 0.4);
        telemetry.addData("LGlis Pos", LGlis.getCurrentPosition());
        telemetry.addData("Target", Target_Pos);
        telemetry.addData("Power", power);
        telemetry.update();



        LGlis.setPower(power);
        RGlis.setPower(power);

    }


}

















