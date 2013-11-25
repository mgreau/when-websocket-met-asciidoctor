package com.mgreau.wwsmad.diff;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.diff.HistogramDiff;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.patch.Patch;


@DiffProvider("JGit")
public class DiffAdocJGit implements DiffAdoc {
	
	@Inject
    private Logger logger;
	
	@Override
	public String rawDiff(String adocSource,
			String adocSourceToMerge) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			RawText rt1 = new RawText(adocSource.getBytes());
			RawText rt2 = new RawText(adocSourceToMerge.getBytes());
			EditList diffList = new EditList();
			diffList.addAll(new HistogramDiff().diff(RawTextComparator.DEFAULT,
					rt1, rt2));
			new DiffFormatter(out).format(diffList, rt1, rt2);
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.toString());
			logger.log(Level.SEVERE, "RawDiff error!");
		}
		return out.toString();
	}

	@Override
	public String applyPatch(String adocSource,
			String patchFile) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			Patch patch = new Patch();
			patch.parse(patchFile.getBytes(), 0, patchFile.getBytes().length);
			patch.getFiles();

		} catch (Exception e) {
			logger.log(Level.SEVERE, e.toString());
			logger.log(Level.SEVERE, "apply patch error!");
		}
		return out.toString();
	}

	

}
