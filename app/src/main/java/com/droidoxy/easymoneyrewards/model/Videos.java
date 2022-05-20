package com.droidoxy.easymoneyrewards.model;

/**
 * Created by DroidOXY
 */
 
public class Videos {

	private String videoId,Title,SubTitle,Amount,vidURL,Status,openLink,Duration,Thumbnail;

	public Videos() {
	}

	public Videos(String videoId, String Title, String SubTitle, String Amount, String vidURL, String Status, String openLink, String Duration, String Thumbnail) {

		this.Title = Title;
		this.SubTitle = SubTitle;
		this.Amount = Amount;
		this.vidURL = vidURL;
		this.Status = Status;
		this.openLink = openLink;
		this.Duration = Duration;
		this.videoId = videoId;
		this.Thumbnail = Thumbnail;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public String getImage() {
		return Thumbnail;
	}

	public void setImage(String Thumbnail) {
		this.Thumbnail = Thumbnail;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String Title) {
		this.Title = Title;
	}

	public String getDuration() {
		return Duration;
	}

	public void setDuration(String Duration) {
		this.Duration = Duration;
	}

	public String getAmount() {
		return Amount;
	}

	public void setAmount(String Amount) {
		this.Amount = Amount;
	}

	public String getVideoURL() {
		return vidURL;
	}

	public void setVideoURL(String vidURL) {
		this.vidURL = vidURL;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String Status) {
		this.Status = Status;
	}

	public String getSubtitle() {
		return SubTitle;
	}

	public void setSubtitle(String SubTitle) {
		this.SubTitle = SubTitle;
	}

	public String getOpenLink() {
		return openLink;
	}

	public void setOpenLink(String openLink) {
		this.openLink = openLink;
	}


}