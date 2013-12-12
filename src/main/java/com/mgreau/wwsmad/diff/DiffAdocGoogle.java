package com.mgreau.wwsmad.diff;

import java.util.LinkedList;
import java.util.logging.Logger;

import javax.inject.Inject;

import com.mgreau.wwsmad.diff.diff_match_patch.Diff;
import com.mgreau.wwsmad.diff.diff_match_patch.Patch;

@DiffProvider("Google")
public class DiffAdocGoogle implements DiffAdoc {

	@Inject
	private Logger logger;
	
	@Inject
	private diff_match_patch diffMatchPatch;

	@Override
	public String applyPatch(String adocSource,
			String patch) {
		LinkedList<Patch> patches = (LinkedList<Patch>)diffMatchPatch.patch_fromText(patch);

		 Object[] results = diffMatchPatch.patch_apply(patches, adocSource);
		 logger.info("return patch");

		return (String)results[0];
	}

	@Override
	public String rawDiff(String adocSource, String adocSourceToMerge) {
		  LinkedList<Diff> diffs = diffMatchPatch.diff_main(adocSource, adocSourceToMerge, true);

		  if (diffs.size() > 2) {
			  diffMatchPatch.diff_cleanupSemantic(diffs);
		  }

		  LinkedList<Patch> patch_list = diffMatchPatch.patch_make(adocSource, diffs);
		  return diffMatchPatch.patch_toText(patch_list);
	}

}
