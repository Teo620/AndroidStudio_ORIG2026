package org.firstinspires.ftc.teamcode.Auto;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.pedropathing.util.Timer;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.geometry.Pose;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.SubSistems.Formula;

import java.util.List;

@Config
@Autonomous(name="Auto_Albastru", group="Linear OpMode")
public class Auto_Albastru extends OpMode {

    private Follower follower;
    private Formula formula = new Formula();

    private Timer pathTimer, opModeTimer,aux,shootTimer,timp_pentru_tras_minge,auxTime,timp_pentru_aruncat_minge;

    public DcMotorEx TURELA = null;
    public DcMotor LIFT = null;
    public DcMotor INTAKE=null;
    private final Pose startPose = new Pose(180,12,Math.toRadians(90));
    private final Pose shootPose = new Pose(59,84,Math.toRadians(0));
    private final Pose GateTake=new Pose(10,65,Math.toRadians(0));
    private final Pose Stem =new Pose(40,15,Math.toRadians(-90));
    private final Pose Polen =new Pose(228,36,Math.toRadians(0));
    private final Pose[] Parkings ={
            new Pose(60,60,Math.toRadians(0)),  //case 1
            new Pose(108,60,Math.toRadians(0)),  //case 2
            new Pose(156,60,Math.toRadians(0))}; //case 3
    private Pose Final_Park=new Pose(0,(0),Math.toRadians(0));
    Servo UNGHITURELA=null;
    Limelight3A limelight;
    IMU imu;
    int lastdistance=0;
    double pozitieservo;
    int TagID;
    int facut=0, iteration=0;
    private PathChain driveStartPosShootPos,driveStartPosPolen1,drivePolenStem1;

    public enum PathState{
        startpos_Polen, Polen_Stem, Stem_Polen2, go_Stem, go_Park, stop;
    }

    PathState pathState;
    Pose lastPose;

    public void buildPaths(){
        driveStartPosPolen1= follower.pathBuilder()
                .addPath(new BezierLine(startPose,Polen))
                .setLinearHeadingInterpolation(startPose.getHeading(),Polen.getHeading())
                .build();

        drivePolenStem1= follower.pathBuilder()
                .addPath(new BezierLine(Polen,Stem))
                .setLinearHeadingInterpolation(Polen.getHeading(),Stem.getHeading())
                .build();


    }

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
                    follower.followPath(Drive(startPose,Polen), true);
                    facut=1;
                }
                if(!follower.isBusy()) {
                    setPathState(PathState.go_Stem);
                }
                break;

            case go_Stem:
                if(facut==0){

                    // arunca mingile
                    follower.followPath(Drive(follower.getPose(),Stem), true);
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

        INTAKE =hardwareMap.get(DcMotor.class,"intake");
        //TURELA =hardwareMap.get(DcMotorEx.class,"turela");
        LIFT =hardwareMap.get(DcMotor.class,"lift");
        imu = hardwareMap.get(IMU.class, "imu");
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        RevHubOrientationOnRobot revHubOrientationOnRobot = new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                RevHubOrientationOnRobot.UsbFacingDirection.UP);
        imu.initialize(new IMU.Parameters(revHubOrientationOnRobot));
        limelight.pipelineSwitch(0);
        limelight.start();

        pathState=PathState.startpos_Polen;
        pathTimer=new Timer();
        opModeTimer=new Timer();
        timp_pentru_tras_minge=new Timer();
        auxTime=new Timer();
        follower= Constants.createFollower(hardwareMap);
        buildPaths();
        follower.setPose(startPose);

    }

    public void init_loop()
    {
        limelight.updateRobotOrientation(Math.toDegrees(follower.getPose().getHeading()));
        LLResult result = limelight.getLatestResult();

        if (result != null && result.isValid()) {
            List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();

            if (!fiducials.isEmpty()) {
                // Lock in the ID to your variable
                TagID = fiducials.get(0).getFiducialId();
            }
        }
    }

    public void start(){
        opModeTimer.resetTimer();
        setPathState(pathState);

        int Case = 0;

        if(TagID==21){
            Case=0;
        }else if(TagID==22){
            Case=1;
        }else{
            Case=2;
        }

        Final_Park=Parkings[Case];
    }

    @Override
    public void loop(){
        follower.update();
        startPathUpdate();
        telemetry.addData("ID OBELISK",TagID);
        telemetry.addData("FACEM LOOP",1);
        telemetry.update();


    }


}

















