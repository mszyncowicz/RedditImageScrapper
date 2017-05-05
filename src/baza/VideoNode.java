package baza;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class VideoNode extends MediaNode {
	private MediaPlayer player;
	private Media media;
	private String url;
	private MediaView viewer;
	private Label playButton;
	
	private boolean isPlaying = false;
	VideoNode(StackPane viewerWrapper, String url, final double widthSetting, final double heightSetting, Stage stage){
		super(viewerWrapper,stage);
		this.url = url;
		this.widthSetting = widthSetting;
		this.heightSetting = heightSetting;
	}
	@Override
	public void setUp() throws MalformedURLException, URISyntaxException {
		
		playButton = new Label("\u25B6");
		playButton.setFont(new Font(150));
		playButton.setMouseTransparent(true);
		playButton.setStyle("-fx-text-fill: #ff1e6d;  -fx-border-insets: 2 0 0 0; -fx-border-width: 2; -fx-effect: dropshadow( gaussian , Black , 0,0,3,3 );");
		setVideo();
		onClick();
	}

	@Override
	public void dispose() {
		 viewerWrapper.getChildren().remove(viewer);
		 if (viewerWrapper.getChildren().contains(playButton)){
			 viewerWrapper.getChildren().remove(playButton);
		 }
		 player.dispose();

	}

	@Override
	public void onClick() {
		this.viewerWrapper.setOnMouseClicked(e -> {
			if (e.getButton().equals(MouseButton.PRIMARY)) {
				if (e.getClickCount() == 2) {
					if (this.isZoomed) {
						isZoomed = false;
						this.viewer.setFitWidth(widthSetting);
					} else {
						this.isZoomed = true;

						this.viewer.setFitWidth(1200);
					}
				}
				if (play())
					this.viewerWrapper.getChildren().remove(playButton);
				else
					this.viewerWrapper.getChildren().add(playButton);
			} else {

				this.player.stop();
			}

			e.consume();
		});

	}
	private boolean play() {
		if (!isPlaying) {
			isPlaying = true;
			player.play();
			System.out.println(isPlaying);
			player.setCycleCount(MediaPlayer.INDEFINITE);
		} else {
			isPlaying = false;
			player.pause();
		}
		return isPlaying;
	}
	
	@Override
	public void setNext(String url) throws MalformedURLException, URISyntaxException {
		dispose();
		this.url = url;
		setVideo();
		
	}
	private void setVideo() throws MalformedURLException, URISyntaxException{
		if (url.charAt(4) == 's') {
			url = url.substring(5);
			url = "http" + url;

		}
		System.out.println(url + " blablabla");
		while (url.charAt(url.length() - 1) != '4') {
			url = url.substring(0, url.length() - 1);
		}
		this.media = new Media(new URL(url).toURI().toURL()
				.toString());
		player = new javafx.scene.media.MediaPlayer(media);
		this.viewer = new MediaView(player);
		this.viewer.setFitWidth(widthSetting);
		player.setOnError(new Runnable() {
			public void run() {
				viewerWrapper.getChildren().remove(playButton);
				Label error = new Label("Video is corrupted");
				viewerWrapper.getChildren().add(error);
				isError = true;
				player.dispose();

			}
		});
		viewerWrapper.getChildren().add(viewer);
		viewerWrapper.getChildren().add(playButton);
		
	}

}
