package com.lunf.laika;

import com.lunf.laika.Utils.Utils;
import com.lunf.laika.core.Bin;
import com.lunf.laika.core.BinPacking;
import com.lunf.laika.primitives.MArea;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

@SpringBootApplication
public class LaikaApplication {

	public static void main(String[] args) {
		SpringApplication.run(LaikaApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

			System.out.println("Let's try to resolve the solution");

			launch("Rectangles.txt");

		};
	}

	private void launch(String fileName) throws IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream in = classLoader.getResourceAsStream(fileName);
		if (in == null) {
			throw new IOException("Could not read file");
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		Object[] result = Utils.loadPieces(reader);

		Dimension binDimension = (Dimension) result[0];
		MArea[] pieces = (MArea[]) result[1];

        System.out.println("Piece " + pieces.length);


		if (pieces == null || pieces.length < 1) {
			System.out.println("NO Pieces for Bin");
			return;
		}

		Bin[] bins = BinPacking.BinPackingStrategy(pieces, binDimension);

		Dimension viewPortDimension = Utils.getViewportDimension(binDimension);
		System.out.println("Generating bin images.........................");
		drawbinToFile(bins, viewPortDimension);
		System.out.println();
		System.out.println("Generating bin description files....................");
		createOutputFiles(bins);
		System.out.println("DONE!!!");

	}

	private void drawbinToFile(Bin[] bins, Dimension viewPortDimension) throws IOException {
		for (int i = 0; i < bins.length; i++) {

			MArea[] areasInThisbin = bins[i].getPlacedPieces();
			ArrayList<MArea> areas = new ArrayList<MArea>();
			for (MArea area : areasInThisbin) {
				areas.add(area);
			}
			Utils.drawMAreasToFile(areas, viewPortDimension, bins[i].getDimension(), ("results/Bin-" + String.valueOf(i + 1)));
			System.out.println("Generated image for bin " + String.valueOf(i + 1));
		}
	}

	private void createOutputFiles(Bin[] bins) throws IOException {
		for (int i = 0; i < bins.length; i++) {
			PrintWriter writer = new PrintWriter("results/Bin-" + String.valueOf(i + 1) + ".txt", "UTF-8");
			writer.println(bins[i].getPlacedPieces().length);
			MArea[] areasInThisbin = bins[i].getPlacedPieces();
			for (MArea area : areasInThisbin) {
				double offsetX = area.getBoundingBox2D().getX();
				double offsetY = area.getBoundingBox2D().getY();
				writer.println(area.getID() + " " + area.getRotation() + " " + offsetX + "," + offsetY);
			}
			writer.close();
			System.out.println("Generated points file for bin " + String.valueOf(i + 1));
		}
	}
}
