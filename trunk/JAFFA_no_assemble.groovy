/***********************************************************
 ** This is the JAFFA pipeline file for fusion detection
 ** without read assembly. Run like so:
 **    bpipe run <path_to_this_file> <path_to_fastq/fasta_files>
 ** See our website for details	on running options:
 ** https://code.google.com/p/jaffa-project/.
 **
 ** Author: Nadia Davidson <nadia.davidson@mcri.edu.au>
 ** Last Update: 6th Feb 2014
 *********************************************************/

commands="trimmomatic R fastx_collapser bowtie2 blat fasta_formatter"

load "JAFFA_stages.groovy"

body = segment { filter_transcripts +
                 extract_fusion_sequences +
                 align_transcripts_to_genome +
                 make_simple_reads_table +
                 get_final_list }

get_unmapped_as_fasta = segment { prepare_reads + cat_reads + remove_dup + get_unmapped }

// below is the pipeline for a fasta file
if(args[0].endsWith(fasta_suffix)) {
   run { run_check + fasta_input_format * [
	     make_dir_using_fasta_name + 
	     align_transcripts_to_annotation.using(tile:contigTile) +
	     body ] + compile_all_results
   } 
// or you can provide the reads and they will be 
// filtered and converted to fasta before running
// the same pipeline as above
} else {
   run { run_check + fastq_input_format * [
       	    make_dir_using_fastq_names +
	    get_unmapped_as_fasta +
	    align_transcripts_to_annotation.using(tile:readTile) +
	    body ] + compile_all_results 
   }
}

