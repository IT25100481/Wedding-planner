package com.wedding.planner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import java.io.*;

@SpringBootApplication
public class PlannerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlannerApplication.class, args);
	}

	/**
	 * AUTO-BOOTSTRAP: Creates default vendors for the Vivaha Exhibition
	 * Runs every time the application starts, but only writes if data is missing.
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void createDefaultVendors() {
		File file = new File("services.txt");

		// Checks if file is missing OR has 0 bytes of data
		if (!file.exists() || file.length() == 0) {
			try (PrintWriter writer = new PrintWriter(new FileWriter(file, false))) {

				// FORMAT: ID|BusinessName|Category|Tradition|Description|Contact|Price|ImagePath|Status

				// --- THE ESSENTIALS ---
				writer.println("e1|The Velvet Crumb|Cakes|Mixed|Luxury tiered cakes featuring hand-painted gold leaf and signature velvet textures.|0775556667|45000|cake.jpg|APPROVED");
				writer.println("e2|Aura Captures|Photography|Mixed|High-end editorial photography specializing in luxury wedding storytelling.|0779998887|125000|photo.jpg|APPROVED");
				writer.println("e3|Grand Ballroom|Venue|Mixed|An exquisite pillar-less ballroom with crystal chandeliers and a private garden terrace.|0112223334|550000|venue.jpg|APPROVED");
				writer.println("e4|Luxe Artistry|Makeup Services|Mixed|Celebrity-grade bridal makeup using premium international brands.|0714445556|65000|makeup.jpg|APPROVED");

				// --- CURATED INSPIRATION ---
				writer.println("v1|Kandyan Heritage|Decoration|Sinhala|Stunning lotus-themed poruwa designs and traditional oil lamp setups for a royal Kandyan experience.|0771112223|150000|kandyan_deco.jpg|APPROVED");
				writer.println("v2|Tamil Arasan Decor|Decoration|Hindu|Intricate wooden mandaps with carved pillars and fresh flower Toranams for a divine Tamil wedding.|0774445556|185000|hindu_deco.jpg|APPROVED");
				writer.println("v3|Mughal Gardens|Decoration|Islamic|Regal Green and Gold drapes with Arabic calligraphy backdrops and elegant Nikah setups.|0778889990|95000|islamic_deco.jpg|APPROVED");
				writer.println("v4|Cathedral Blooms|Decoration|Christian|Classic white floral cathedral isle designs and garden-themed reception setups.|0712223334|110000|church_deco.jpg|APPROVED");

				System.out.println("VIVAHA SYSTEM: Default Exhibition Vendors have been initialized.");

			} catch (IOException e) {
				System.err.println("Error creating default data: " + e.getMessage());
			}
		}
	}
}