package baza;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import javafx.scene.input.ClipboardContent;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import jfxtras.labs.util.event.MouseControlUtil;


public class Album {
	private String[] album;
	private int current = 0;
	private Integer vote;
	private String author;
	private String title, url;

	
	
	private Button buttonNext;
	private Button buttonPrev;
	private Button buttonLike;
	private HBox albumPane;
	private StackPane viewerWrapper;

	private Hyperlink authorLink;
	private Label playButton;
	
	private WindowScreen app;
	
	private MediaNode media;
	
	private boolean isZoomed = false;
	private boolean isFav = false;
	private boolean isAlbum = true;
	private boolean debug = true;
	private boolean isPlaying = false;
	private boolean isVideo = false;
	
	final double widthSetting = 800;
	
	public Album(String photo, WindowScreen app) {
		setApp(app);
		isAlbum = false;
		this.album = new String[1];
		this.album[0] = photo;
		System.out.println("to: " + photo);
		this.viewerWrapper = new StackPane();
		if (photo != null) this.setPhoto(photo);
	}

	public Album(String[] album, WindowScreen app) {
		setApp(app);
		if (album.length < 2)
			isAlbum = false;
		this.album = album;
		this.viewerWrapper = new StackPane();
		if (album[0] != null)
			this.setPhoto(album[0]);
	}

	public String[] getAlbum() {
		return album;
	}

	public void setAlbum(String[] album) {
		this.album = album;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getTitle() {
		return title;
	}

	public String getUrl() {
		return url;
	}

	public void setAuthorLink(Hyperlink authorLink) {
		this.authorLink = authorLink;
	}

	Album(WindowScreen app) {
		setApp(app);
	}

	public void setApp(WindowScreen app) {
		this.app = app;
	}

	public void setPhoto(String photo) {
		System.out.println(photo);
		if (photo.contains(".mp4")) {
			try {
				media = new VideoNode(viewerWrapper, photo, 700, 0 ,app.getPrimaryStage());
				media.setUp();
				isVideo = true;
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			try{
				media = new ImageNode(viewerWrapper, photo, 700, 0 ,app.getPrimaryStage());
				media.setUp();
			}catch (Exception e){
				e.printStackTrace();
			}
			
		}
		
	}

	

	public void remove() {
		try {
			media.dispose();
			this.album = null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		// System.gc();
	}

	private int next() {
		do {
			if (current + 1 == album.length) {
				current = 0;
			} else {
				current++;
			}
		} while (album[current].isEmpty());
		prepareForPhotoChange();
		return current;
	}

	private void prepareForPhotoChange() {
		if (!isVideo) {
			if (album[current].contains(".jpg")
					|| album[current].contains(".gif")
					|| album[current].contains(".png")) {
				Platform.runLater(new Runnable() {
					public void run() {
						try {
							media.setNext(album[current]);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}

				});
			} else {
				media.dispose();
				setPhoto(album[current]);
			}

		}
		else {
			if (album[current].contains(".mp4")){
				Platform.runLater(new Runnable() {
					public void run() {
						try {
							media.setNext(album[current]);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}

				});
			} else {
				isVideo = false;
				media.dispose();
				setPhoto(album[current]);
			}
		}

		System.out.println(current + " " + album.length);

	}

	private int prev() {
		do {
			if (current - 1 < 0) {
				current = album.length - 1;
			} else {
				current--;
			}
		} while (album[current].isEmpty());
		prepareForPhotoChange();
		return current;
	}

	

	public void setUrl(String url) {
		this.url = url;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setVote(Integer vote) {
		this.vote = vote;
	}

	public boolean isZoomed() {
		return isZoomed;
	}

	public Hyperlink getAuthorLink() {
		return this.authorLink;
	}

	

	public HBox getAlbumPane() {
		buttonNext = new Button();
		buttonPrev = new Button();
		buttonNext.setText("next");
		buttonPrev.setText("prev");
		buttonLike = new Button();
		buttonLike.setText("+");
		Button download = new Button();
		buttonNext.setMinWidth(200);
		buttonPrev.setMinWidth(200);
		VBox albumBox = new VBox();

		albumPane = new HBox();
		albumBox.setAlignment(Pos.CENTER);

		Label title = new Label();
		title.setText(this.title);
		title.setId("Title");

		Hyperlink url = new Hyperlink();
		url.setText(this.url);
		url.setOnAction(t -> {
			app.getHostServices().showDocument(url.getText());
		});
		url.setId("Url");
		Label vote = null;
		if (this.author != null) {
			authorLink = new Hyperlink();

			authorLink.setText(this.author.toString());
			authorLink.setId("Vote");
			albumBox.setId("Content");
		}

		Album a = this;
		albumBox.getChildren().addAll(title);
		if (this.author != null) {
			albumBox.getChildren().addAll(authorLink);
		}
		albumBox.getChildren().add(this.viewerWrapper);
		
		buttonLike.setId("Like");

		buttonLike.setOnAction(e -> {

			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					if (app.getBaza().hasFolder()) {
						if (!app.getBaza().isInFolder(a)) {
							if (app.getBaza().insertAlbum(a)) {
								buttonLike.setStyle("-fx-text-fill:Crimson;-fx-font-size: 12px; ");
							} else {
								Alert alert = new Alert(AlertType.INFORMATION);
								alert.setTitle("Error");
								alert.setHeaderText("Database Error!!");
								alert.setContentText("Its our fault lol");

								alert.showAndWait();
							}
						} else {
							if (app.getBaza().deleteAlbum(a.url)) {
								buttonLike.setStyle("-fx-text-fill:black; -fx-font-size: 18px");
							} else {
								Alert alert = new Alert(AlertType.INFORMATION);
								alert.setTitle("Error");
								alert.setHeaderText("Database Error!!");
								alert.setContentText("Its our fault lol");

								alert.showAndWait();
							}
						}

					} else {
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("No folder selected");
						alert.setHeaderText("No folder selected");
						alert.setContentText("You have to select  folder first!");

						alert.showAndWait();
					}
					albumPane.getParent().requestFocus();

				}
			});
			e.consume();
		});

		download.setText("\u25BC");
		download.setId("Like");
		download.setOnAction(e -> {
			new Thread(new Runnable() {
				public void run() {
					String path = System.getProperty("user.dir").toString();
					String folder = "/Downloads/picvids/";
					new File(path + folder).mkdirs();
					
					
					try {
					
						
						URL zet = new URL(a.album[current]);
						String fpath = zet.getFile().replace('/', '1');
						File sa = new File(path + folder + fpath);
						System.out.println(sa.getAbsolutePath());
						sa.createNewFile();
						FileUtils.copyURLToFile(zet, sa);
						System.out.println(sa.getAbsolutePath());
					} catch (Exception e2) {
				
						
						e2.printStackTrace();
					}
				}
			}).start();
			albumPane.getParent().requestFocus();
		});
		HBox actionBarForLikesAndDownloads = new HBox();
		actionBarForLikesAndDownloads.getChildren().addAll(buttonLike, download);
		this.viewerWrapper.setOnMouseEntered(e -> {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					viewerWrapper.getChildren().add(actionBarForLikesAndDownloads);
					StackPane.setAlignment(actionBarForLikesAndDownloads, Pos.TOP_LEFT);
					if (app.getBaza().isInFolder(a)) {
						buttonLike.setStyle("-fx-text-fill:Crimson; ");
					} else {
						buttonLike.setStyle("-fx-font-size: 18px;");
					}

				}
			});
			e.consume();
		});
		this.viewerWrapper.setOnMouseExited(e -> {
			this.viewerWrapper.getChildren().remove(actionBarForLikesAndDownloads);
			e.consume();
		});

		buttonNext.setOnAction(e -> {

			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					next();

				}
			});
			t.start();
			e.consume();
		});
		buttonPrev.setOnAction(e -> {

			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					prev();

				}
			});
			t.start();
			e.consume();
		});

		albumPane.setAlignment(Pos.CENTER);

		albumBox.getChildren().add(url);
		albumBox.setMaxHeight(600);
		albumBox.setMaxWidth(widthSetting);
		// layout.getChildren().add(layout);
		if (this.isAlbum) {
			albumPane.getChildren().add(buttonPrev);
			albumPane.getChildren().add(albumBox);
			albumPane.getChildren().add(buttonNext);
		} else albumPane.getChildren().add(albumBox);

		albumPane.setMaxSize(widthSetting, 600);
		
		 
		
		// MouseControlUtil.makeDraggable(this.viewerWrapper);

		albumPane.setPadding(new Insets(0, 0, 0, 0));
		albumBox.setPadding(new Insets(0, 0, 0, 0));
		albumPane = albumPane;
		return albumPane;
	}


}
