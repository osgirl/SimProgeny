#! /usr/bin/perl

use strict;
use List::Util qw(shuffle);
use Time::HiRes;
use node_v8;

my $ped_file = shift;
my $ascertained_samples_file = shift;
my %order_group_sizes;

my @ascertained_samples = load_ascertained_samples($ascertained_samples_file,\%order_group_sizes);
my @ascertained_samples_first_61K = @ascertained_samples[1..61019];
#print "Ascertained_samples: @ascertained_samples\n";
my %samples_hash = map {$_ => 1} @ascertained_samples;
my %samples_first_61K = map {$_ => 1} @ascertained_samples_first_61K;
my @network_refs_from_fam = build_network_from_fam_file($ped_file,\%samples_hash);
write_out_relationships("$ascertained_samples_file.relationships",\%samples_first_61K,@network_refs_from_fam);
analyze("$ascertained_samples_file.analyzed.txt",\%samples_hash,\@ascertained_samples,\%order_group_sizes,@network_refs_from_fam);










###############################################################################################
###############################################################################################
###############################################################################################
sub analyze
{
  print "Analyzing...\n";
  my $outfile = shift;
  my $samples_ref = shift;
  my $samples_arr_ref = shift;
  my $order_group_sizes_ref = shift;
	my @network_refs = @_;
  
  my %sample_to_network;

  ## Make network lookup
	foreach my $network_ref (@network_refs)
	{
		foreach my $node_name (keys %{$network_ref})
		{
      $sample_to_network{$node_name} = $network_ref;
    }
  }
  
  #########
  ## Get the pool for each ordered group 
  my $sample_ctr = 0; ## used to keep track of position in the original ascertainment list
  my $network_ctr = 1;

  my %temp_ascertainment_pool;
  my $size_temp_ascertainment_pool = 0;
  my $num_1st_rels = 0;
  my $num_2nd_rels = 0;
  my $num_3rd_rels = 0;

  my %has_1st_rel;
  my %has_2nd_rel;
  my %has_3rd_rel;

  my %networks_1st;
  my %networks_2nd;
  my %networks_3rd;

  my $largest_1st_network;
  my $largest_2nd_network;
  my $largest_3rd_network;

  my $largest_1st_network_size = 0;
  my $largest_2nd_network_size = 0;
  my $largest_3rd_network_size = 0;

  my %id_to_network_1st;
  my %id_to_network_2nd;
  my %id_to_network_3rd;

  my $num_trios = 0;

  open(OUT,">$outfile") or die "can't open analyzed file: $!\n";

  print OUT "size_temp_ascertainment_pool\tnum_1st_rels\tnum_2nd_rels\tnum_3rd_rels\tproportion_1st\tproportion_2nd\tproportion_3rd\tlargest_1st_network_size\tlargest_2nd_network_size\tlargest_3rd_network_size\tnum_trios\n";


  my $tt_time = 0;
  my $t0_time = 0;
  my $t1_time = 0;
  my $t2_time = 0;
  my $t3_time = 0;
  my $t4_time = 0;
  my $t5_time = 0;
  my $t6_time = 0;


  for my $order_num (1..1001)
  {
    ## Collect samples from the order group
    my @order_pool = ();
    for(my $j = 0; $j < $$order_group_sizes_ref{$order_num}; $j++)
    {
      ## Add one sample at a time and calculate results
      my $sample = @$samples_arr_ref[$sample_ctr];
      push(@order_pool,$sample);
      $sample_ctr++;
    }
    next if @order_pool < 1;

    print "Order pool $order_num: " . @order_pool . "\n";

    ## Shuffle samples and then cycle through them one by one adding them to the temp ascertainment pool and calcualting stats
    ## Don't shuffle if it is the last group, unless the last group is also the first group
    if ($order_num != 1001 || $size_temp_ascertainment_pool == 0)
    {
      @order_pool = shuffle(@order_pool);
    }

    foreach my $sample (@order_pool)
    {
      next if $sample eq "";

      #my $tt = [Time::HiRes::gettimeofday];
      #my $t0 = [Time::HiRes::gettimeofday];

      #print "Sample $sample\n";
      $temp_ascertainment_pool{$sample} = 1;
      $size_temp_ascertainment_pool++;
		  
      if($size_temp_ascertainment_pool%1000 == 0)
      {
        print "Samples analyzed $size_temp_ascertainment_pool\n" if $size_temp_ascertainment_pool%1000 == 0;
        #print "total: $tt_time\n";
        #print "sum:". 100*($t0_time + $t1_time + $t2_time + $t3_time + $t4_time + $t5_time + $t6_time)/$tt_time ."\n";
        #print "t0: $t0_time(" . 100*$t0_time/$tt_time .")\n";
        #print "t1: $t1_time(" . 100*$t1_time/$tt_time .")\n";
        #print "t2: $t2_time(" . 100*$t2_time/$tt_time .")\n";
        #print "t3: $t3_time(" . 100*$t3_time/$tt_time .")\n";
        #print "t4: $t4_time(" . 100*$t4_time/$tt_time .")\n";
        #print "t5: $t5_time(" . 100*$t5_time/$tt_time .")\n";
        #print "t6: $t6_time(" . 100*$t6_time/$tt_time .")\n";
      }

      ## Add the sample to its own (temp ascertainment) networks
      if(!exists $id_to_network_1st{$sample})
      {
			  push @{ $networks_1st{$network_ctr} }, "$sample";
			  push @{ $networks_2nd{$network_ctr} }, "$sample";
			  push @{ $networks_3rd{$network_ctr} }, "$sample";
        $id_to_network_1st{$sample} = $network_ctr;
        $id_to_network_2nd{$sample} = $network_ctr;
        $id_to_network_3rd{$sample} = $network_ctr;
        $network_ctr++;
      }
      
      #$t0_time += Time::HiRes::tv_interval($t0);
      #my $t1 = [Time::HiRes::gettimeofday];
      
      my $network_ref = $sample_to_network{$sample};
      my %PC_rels = $$network_ref{$sample}->pc();
      my %FS_rels = $$network_ref{$sample}->fs();
      my %HAG_rels = $$network_ref{$sample}->hag();
      my %CGH_rels = $$network_ref{$sample}->cgh();

      #### Check if adding sample formed a new trio
      my @parents = $$network_ref{$sample}->parents();
      my @children = $$network_ref{$sample}->children();
      ## Check if it is the child
      if(@parents == 2 && exists $temp_ascertainment_pool{@parents[0]} && exists $temp_ascertainment_pool{@parents[1]})
      {
        $num_trios++;
      }
      ## Check if it is the parent
      foreach my $child (@children)
      {
        next if !exists $temp_ascertainment_pool{$child};
        my @parents = $$network_ref{$child}->parents();
        if(@parents == 2 && exists $temp_ascertainment_pool{@parents[0]} && exists $temp_ascertainment_pool{@parents[1]})
        {
          $num_trios++;
        }
      }

      #$t1_time += Time::HiRes::tv_interval($t1);

      #my $t2 = [Time::HiRes::gettimeofday];

      ##### PC REL ADDITION
      foreach my $rel (keys %PC_rels)
      {
        ### Check if new relationship(s) made
        next if !exists $temp_ascertainment_pool{$rel};

        $has_1st_rel{$sample}++;
        $has_1st_rel{$rel}++;
        $has_2nd_rel{$sample}++;
        $has_2nd_rel{$rel}++;
        $has_3rd_rel{$sample}++;
        $has_3rd_rel{$rel}++;
        $num_1st_rels++;
          
        ### Check to see if this sample formed a new largest network by adding another edge between it and rel
        my $sample_1st_network_num = $id_to_network_1st{$sample};
        my $sample_2nd_network_num = $id_to_network_2nd{$sample};
        my $sample_3rd_network_num = $id_to_network_3rd{$sample};
		    my $rel_1st_network_num = $id_to_network_1st{$rel};
		    my $rel_2nd_network_num = $id_to_network_2nd{$rel};
		    my $rel_3rd_network_num = $id_to_network_3rd{$rel};
        
        ## if not in the same 1st degree network
        #print "$sample ($sample_1st_network_num) $rel ($rel_1st_network_num)\n";
        
        if($rel_1st_network_num ne $sample_1st_network_num)
        {
          # combine $sample_network and $rel_network
          my @sample_network = @{ $networks_1st{$sample_1st_network_num} };
          my @rel_network = @{ $networks_1st{$rel_1st_network_num} };
          my @new_array = (@sample_network,@rel_network);
          $networks_1st{$rel_1st_network_num} = [@new_array];
          
          #print "\nSample net: @sample_network\n";
          #print "Rel net: @rel_network\n";
          #print "new net: @new_array\n";
          delete $networks_1st{$sample_1st_network_num};


          foreach my $id (@sample_network)
          {
            my $old_network = $id_to_network_1st{$id};
            if($old_network ne $sample_1st_network_num)
            {
              print "ERROR!!! $id claims to be in $old_network; actually in $sample_1st_network_num\n";
              exit;
            }
            $id_to_network_1st{$id} = $rel_1st_network_num;
          }
          
          
          # Update largest network size if necessary
          if(@{ $networks_1st{$rel_1st_network_num} } > $largest_1st_network_size)
          {
            $largest_1st_network_size = @{ $networks_1st{$rel_1st_network_num} };
            $largest_1st_network = $rel_1st_network_num;
          }
        }
        if($rel_2nd_network_num ne $sample_2nd_network_num)
        {
          # combine $sample_network and $rel_network
          my @sample_network = @{ $networks_2nd{$sample_2nd_network_num} };
          my @rel_network = @{ $networks_2nd{$rel_2nd_network_num} };
          my @new_array = (@sample_network,@rel_network);
          $networks_2nd{$rel_2nd_network_num} = [@new_array];
          delete $networks_2nd{$sample_2nd_network_num};

          foreach my $id (@sample_network)
          {
            my $old_network = $id_to_network_2nd{$id};
            if($old_network ne $sample_2nd_network_num)
            {
              print "ERROR!!! $id claims to be in $old_network; actually in $sample_2nd_network_num\n";
              exit;
            }
            $id_to_network_2nd{$id} = $rel_2nd_network_num;
          }
          
          # Update largeset network size if necessary
          if(@{ $networks_2nd{$rel_2nd_network_num} } > $largest_2nd_network_size)
          {
            $largest_2nd_network_size = @{ $networks_2nd{$rel_2nd_network_num} };
            $largest_2nd_network = $rel_2nd_network_num;
          }
        }
        if($rel_3rd_network_num ne $sample_3rd_network_num)
        {
          # combine $sample_network and $rel_network
          my @sample_network = @{ $networks_3rd{$sample_3rd_network_num} };
          my @rel_network = @{ $networks_3rd{$rel_3rd_network_num} };
          my @new_array = (@sample_network,@rel_network);
          $networks_3rd{$rel_3rd_network_num} = [@new_array];
          delete $networks_3rd{$sample_3rd_network_num};

          foreach my $id (@sample_network)
          {
            my $old_network = $id_to_network_3rd{$id};
            if($old_network ne $sample_3rd_network_num)
            {
              print "ERROR!!! $id claims to be in $old_network; actually in $sample_3rd_network_num\n";
              exit;
            }
            $id_to_network_3rd{$id} = $rel_3rd_network_num;
          }
          
          # Update largeset network size if necessary
          if(@{ $networks_3rd{$rel_3rd_network_num} } > $largest_3rd_network_size)
          {
            $largest_3rd_network_size = @{ $networks_3rd{$rel_3rd_network_num} };
            $largest_3rd_network = $rel_3rd_network_num;
          }
        }
      }
      #$t2_time += Time::HiRes::tv_interval($t2);

      #my $t3 = [Time::HiRes::gettimeofday];
      ##### FS REL ADDITION
      foreach my $rel (keys %FS_rels)
      {
        ### Check if new relationship(s) made
        next if !exists $temp_ascertainment_pool{$rel};

        $has_1st_rel{$sample}++;
        $has_1st_rel{$rel}++;
        $has_2nd_rel{$sample}++;
        $has_2nd_rel{$rel}++;
        $has_3rd_rel{$sample}++;
        $has_3rd_rel{$rel}++;
        $num_1st_rels++;
        
        ### Check to see if this sample formed a new largest network by adding another edge between it and rel
        my $sample_1st_network_num = $id_to_network_1st{$sample};
        my $sample_2nd_network_num = $id_to_network_2nd{$sample};
        my $sample_3rd_network_num = $id_to_network_3rd{$sample};
		    my $rel_1st_network_num = $id_to_network_1st{$rel};
		    my $rel_2nd_network_num = $id_to_network_2nd{$rel};
		    my $rel_3rd_network_num = $id_to_network_3rd{$rel};
        
        if($rel_1st_network_num ne $sample_1st_network_num)
        {
          # combine $sample_network and $rel_network
          my @sample_network = @{ $networks_1st{$sample_1st_network_num} };
          my @rel_network = @{ $networks_1st{$rel_1st_network_num} };
          my @new_array = (@sample_network,@rel_network);
          $networks_1st{$rel_1st_network_num} = [@new_array];
          delete $networks_1st{$sample_1st_network_num};

          foreach my $id (@sample_network)
          {
            my $old_network = $id_to_network_1st{$id};
            if($old_network ne $sample_1st_network_num)
            {
              print "ERROR!!! $id claims to be in $old_network; actually in $sample_1st_network_num\n";
              exit;
            }
            $id_to_network_1st{$id} = $rel_1st_network_num;
          }
          
          # Update largeset network size if necessary
          if(@{ $networks_1st{$rel_1st_network_num} } > $largest_1st_network_size)
          {
            $largest_1st_network_size = @{ $networks_1st{$rel_1st_network_num} };
            $largest_1st_network = $rel_1st_network_num;
          }
        }
        if($rel_2nd_network_num ne $sample_2nd_network_num)
        {
          # combine $sample_network and $rel_network
          my @sample_network = @{ $networks_2nd{$sample_2nd_network_num} };
          my @rel_network = @{ $networks_2nd{$rel_2nd_network_num} };
          my @new_array = (@sample_network,@rel_network);
          $networks_2nd{$rel_2nd_network_num} = [@new_array];
          delete $networks_2nd{$sample_2nd_network_num};

          foreach my $id (@sample_network)
          {
            my $old_network = $id_to_network_2nd{$id};
            if($old_network ne $sample_2nd_network_num)
            {
              print "ERROR!!! $id claims to be in $old_network; actually in $sample_2nd_network_num\n";
              exit;
            }
            $id_to_network_2nd{$id} = $rel_2nd_network_num;
          }
          
          # Update largeset network size if necessary
          if(@{ $networks_2nd{$rel_2nd_network_num} } > $largest_2nd_network_size)
          {
            $largest_2nd_network_size = @{ $networks_2nd{$rel_2nd_network_num} };
            $largest_2nd_network = $rel_2nd_network_num;
          }
        }
        if($rel_3rd_network_num ne $sample_3rd_network_num)
        {
          # combine $sample_network and $rel_network
          my @sample_network = @{ $networks_3rd{$sample_3rd_network_num} };
          my @rel_network = @{ $networks_3rd{$rel_3rd_network_num} };
          my @new_array = (@sample_network,@rel_network);
          $networks_3rd{$rel_3rd_network_num} = [@new_array];
          delete $networks_3rd{$sample_3rd_network_num};

          foreach my $id (@sample_network)
          {
            my $old_network = $id_to_network_3rd{$id};
            if($old_network ne $sample_3rd_network_num)
            {
              print "ERROR!!! $id claims to be in $old_network; actually in $sample_3rd_network_num\n";
              exit;
            }
            $id_to_network_3rd{$id} = $rel_3rd_network_num;
          }
          
          # Update largeset network size if necessary
          if(@{ $networks_3rd{$rel_3rd_network_num} } > $largest_3rd_network_size)
          {
            $largest_3rd_network_size = @{ $networks_3rd{$rel_3rd_network_num} };
            $largest_3rd_network = $rel_3rd_network_num;
          }
        }
      }
      #$t3_time += Time::HiRes::tv_interval($t3);
      
      #my $t4 = [Time::HiRes::gettimeofday];
      ##### HAG REL ADDITION
      foreach my $rel (keys %HAG_rels)
      {
        ### Check if new relationship(s) made
        next if !exists $temp_ascertainment_pool{$rel};

        $has_2nd_rel{$sample}++;
        $has_2nd_rel{$rel}++;
        $has_3rd_rel{$sample}++;
        $has_3rd_rel{$rel}++;
        $num_2nd_rels++;
          
        ### Check to see if this sample formed a new largest network by adding another edge between it and rel
        my $sample_2nd_network_num = $id_to_network_2nd{$sample};
        my $sample_3rd_network_num = $id_to_network_3rd{$sample};
		    my $rel_2nd_network_num = $id_to_network_2nd{$rel};
		    my $rel_3rd_network_num = $id_to_network_3rd{$rel};
        
        if($rel_2nd_network_num ne $sample_2nd_network_num)
        {
          # combine $sample_network and $rel_network
          my @sample_network = @{ $networks_2nd{$sample_2nd_network_num} };
          my @rel_network = @{ $networks_2nd{$rel_2nd_network_num} };
          my @new_array = (@sample_network,@rel_network);
          $networks_2nd{$rel_2nd_network_num} = [@new_array];
          delete $networks_2nd{$sample_2nd_network_num};

          foreach my $id (@sample_network)
          {
            my $old_network = $id_to_network_2nd{$id};
            if($old_network ne $sample_2nd_network_num)
            {
              print "ERROR!!! $id claims to be in $old_network; actually in $sample_2nd_network_num\n";
              exit;
            }
            $id_to_network_2nd{$id} = $rel_2nd_network_num;
          }
          
          # Update largeset network size if necessary
          if(@{ $networks_2nd{$rel_2nd_network_num} } > $largest_2nd_network_size)
          {
            $largest_2nd_network_size = @{ $networks_2nd{$rel_2nd_network_num} };
            $largest_2nd_network = $rel_2nd_network_num;
          }
        }
        if($rel_3rd_network_num ne $sample_3rd_network_num)
        {
          # combine $sample_network and $rel_network
          my @sample_network = @{ $networks_3rd{$sample_3rd_network_num} };
          my @rel_network = @{ $networks_3rd{$rel_3rd_network_num} };
          my @new_array = (@sample_network,@rel_network);
          $networks_3rd{$rel_3rd_network_num} = [@new_array];
          delete $networks_3rd{$sample_3rd_network_num};

          foreach my $id (@sample_network)
          {
            my $old_network = $id_to_network_3rd{$id};
            if($old_network ne $sample_3rd_network_num)
            {
              print "ERROR!!! $id claims to be in $old_network; actually in $sample_3rd_network_num\n";
              exit;
            }
            $id_to_network_3rd{$id} = $rel_3rd_network_num;
          }
          
          # Update largeset network size if necessary
          if(@{ $networks_3rd{$rel_3rd_network_num} } > $largest_3rd_network_size)
          {
            $largest_3rd_network_size = @{ $networks_3rd{$rel_3rd_network_num} };
            $largest_3rd_network = $rel_3rd_network_num;
          }
        }
      }
      #$t4_time += Time::HiRes::tv_interval($t4);

      #my $t5 = [Time::HiRes::gettimeofday];

      ##### CGH REL ADDITION
      foreach my $rel (keys %CGH_rels)
      {
        ### Check if new relationship(s) made
        next if !exists $temp_ascertainment_pool{$rel};

        $has_3rd_rel{$sample}++;
        $has_3rd_rel{$rel}++;
        $num_3rd_rels++;
          
        ### Check to see if this sample formed a new largest network by adding another edge between it and rel
        my $sample_3rd_network_num = $id_to_network_3rd{$sample};
		    my $rel_3rd_network_num = $id_to_network_3rd{$rel};
        
        if($rel_3rd_network_num ne $sample_3rd_network_num)
        {
          # combine $sample_network and $rel_network
          my @sample_network = @{ $networks_3rd{$sample_3rd_network_num} };
          my @rel_network = @{ $networks_3rd{$rel_3rd_network_num} };
          my @new_array = (@sample_network,@rel_network);
          $networks_3rd{$rel_3rd_network_num} = [@new_array];
          delete $networks_3rd{$sample_3rd_network_num};

          foreach my $id (@sample_network)
          {
            my $old_network = $id_to_network_3rd{$id};
            if($old_network ne $sample_3rd_network_num)
            {
              print "ERROR!!! $id claims to be in $old_network; actually in $sample_3rd_network_num\n";
              exit;
            }
            $id_to_network_3rd{$id} = $rel_3rd_network_num;
          }
          
          # Update largeset network size if necessary
          if(@{ $networks_3rd{$rel_3rd_network_num} } > $largest_3rd_network_size)
          {
            $largest_3rd_network_size = @{ $networks_3rd{$rel_3rd_network_num} };
            $largest_3rd_network = $rel_3rd_network_num;
          }
        }
      }
      #$t5_time += Time::HiRes::tv_interval($t5);
     
      #my $t6 = [Time::HiRes::gettimeofday];

      #print "$size_temp_ascertainment_pool\n" if $size_temp_ascertainment_pool%10000 == 0;
      if($size_temp_ascertainment_pool%1 == 0)
      {
        my $num_with_1st = keys %has_1st_rel;

        #my %temp2 = (%has_1st_rel, %has_2nd_rel);
        #my %temp3 = (%has_1st_rel, %has_2nd_rel, %has_3rd_rel);
        my $num_with_1st_to_2nd = keys %has_2nd_rel;
        my $num_with_1st_to_3rd = keys %has_3rd_rel;
        my $proportion_1st = $num_with_1st / $size_temp_ascertainment_pool;
        my $proportion_2nd = $num_with_1st_to_2nd / $size_temp_ascertainment_pool;
        my $proportion_3rd = $num_with_1st_to_3rd / $size_temp_ascertainment_pool;

        my $line = "$size_temp_ascertainment_pool\t$num_1st_rels\t$num_2nd_rels\t$num_3rd_rels\t$proportion_1st\t$proportion_2nd\t$proportion_3rd\t$largest_1st_network_size\t$largest_2nd_network_size\t$largest_3rd_network_size\t$num_trios";
      
        print OUT "$line\n";
      }
      #$t6_time += Time::HiRes::tv_interval($t6);
      #$tt_time += Time::HiRes::tv_interval($tt);
    }
  }
  close(OUT);
}


sub write_out_relationships
{
	my $file = shift;
  my $samples_ref = shift;
  #return;
	print "Writing relationships to $file\n";
	my @network_refs = @_;
	open(OUT,">$file") or die "can't open the relationships out file: $!\n";
	print OUT "IID1\tIID2\tRELATIONSHIP\n";
	my %added;
	
  my $ctr = 0;
	
	
	foreach my $network_ref (@network_refs)
	{
		foreach my $node_name (keys %{$network_ref})
		{
      ## Only write out samples that were ascertained
      next if ! exists $$samples_ref{$node_name};
      $ctr++;

      #my ($FID1, $IID1) = split(/__/,$node_name);
			my %rels = $$network_ref{$node_name}->relatives();
			foreach my $rel_name (keys %rels)
			{
        ## Only write out samples that were ascertained
        next if ! exists $$samples_ref{$rel_name};

        ## Skip if rel is itself
				if($rel_name eq $node_name){next;}
				
				my $rel = $rels{$rel_name};
        next if ($rel eq "" || !($rel eq "PC" || $rel eq "FS" || $rel eq "HAG") );
        
        ## Skip if already added
				if(exists $added{"$node_name-$rel_name"}){next;}

			  print OUT "$node_name\t$rel_name\t$rel\n";

				$added{"$node_name-$rel_name"} = 1;
				$added{"$rel_name-$node_name"} = 1;
			}
      print "  Samples processed $ctr\n" if $ctr%1000 == 0;
		}
	}
}


sub load_ascertained_samples
{
  my $file = shift;
  my $order_group_sizes_ref = shift;

  my @samples;
  open(IN,$file) or die "can't open ascertained sample file: $!\n";
  while(my $line = <IN>)
  {
    chomp($line);
    my ($sample,$order_group) = split(/\s+/,$line);
    $order_group = 1001 if $order_group eq "" || $order_group == -1;
    $$order_group_sizes_ref{$order_group}++;
    push(@samples,$sample);
  }
  return @samples;
}


sub build_network_from_fam_file
{
	my $fam_file = shift;
  my $ascertained_samples_ref = shift;

	print "Building network for $fam_file\n";
	
	## Read in file
	open(IN,$fam_file);
	my %all_nodes_network;
	my $network_ref = \%all_nodes_network;
	my @network_refs;
	
  my $ctr = 0;
	## Build pedigree
	while(my $line = <IN>)
	{
    $ctr++;
    print "  Samples processed $ctr\n" if $ctr%20000 == 0;
		chomp($line);
		my ($FID,$IID,$PID,$MID,$SEX,$PHENOTYPE) = split(/\s+/,$line);
		my $child = "$IID";
		my $mom = "$MID";
		my $dad = "$PID";
		
		if(!exists $$network_ref{$child})
		{
			my $node = new node_v8($child);
			$$network_ref{$child} = $node;
		}
		if(!exists $$network_ref{$dad} && $PID ne 0)
		{
			my $node = new node_v8($dad);
			$$network_ref{$dad} = $node;
		}
		if(!exists $$network_ref{$mom} && $MID ne 0)
		{
			my $node = new node_v8($mom);
			$$network_ref{$mom} = $node;
		}
		if($PID ne 0)
		{
			$$network_ref{$child}->add_parent($dad);
			$$network_ref{$dad}->add_child($child);
		}
		
		if($MID ne 0)
		{
			$$network_ref{$child}->add_parent($mom);
			$$network_ref{$mom}->add_child($child);
		}
	}
	close(IN);


	## Break network into individual pedigrees and
	## Build relationships
  print "Breaking into pedigrees...\n";
	my @keys = keys %$network_ref;
  #my $get_subpedigree_names_time = 0;
  #my $delete_time = 0;
  #my $total_time = 0;
  #my $t0_time = 0;
  #my $t3_time = 0;
  #my $t4_time = 0;
  
  foreach my $node_name (@keys)
  {
    next if ! exists $$network_ref{$node_name};
    #my $tt = [Time::HiRes::gettimeofday];
    #my $t0 = [Time::HiRes::gettimeofday];
    #print "Keys: " . @keys . " \n";
    #if($ctr%10 == 0)
    #{
    #  print "\nKeys processed $ctr\n";
    #  print "total: $total_time\n";
    #  print "sum = " . 100*($t0_time + $delete_time + $get_subpedigree_names_time + $t3_time + $t4_time)/$total_time . "\n";
    #  print "t0: $t0_time(" . 100*$t0_time/$total_time .")\n";
    #  print "subpedigree: $get_subpedigree_names_time(" . 100*$get_subpedigree_names_time/$total_time .")\n";
    #  print "delete: $delete_time(" . 100*$delete_time/$total_time .")\n";
    #  print "t3: $t3_time(" . 100*$t3_time/$total_time .")\n";
    #  print "t4: $t4_time(" . 100*$t4_time/$total_time .")\n";
    #}
    #my $node_name = @keys[0];
    
    #$t0_time += Time::HiRes::tv_interval($t0);
    #my $t1 = [Time::HiRes::gettimeofday];
    my %names = $$network_ref{$node_name}->get_subpedigree_names($network_ref);
    #$get_subpedigree_names_time += Time::HiRes::tv_interval($t1);

    #my $t2 = [Time::HiRes::gettimeofday];
    my %pedigree;
    foreach my $node_name (keys %names)
    {
      $pedigree{$node_name} = $$network_ref{$node_name};
      delete $$network_ref{$node_name};
    }
    #$delete_time += Time::HiRes::tv_interval($t2);
    #my $t3 = [Time::HiRes::gettimeofday];
    push(@network_refs,\%pedigree);
    #$t3_time += Time::HiRes::tv_interval($t3);

    #my $t4 = [Time::HiRes::gettimeofday];
    #@keys = keys %$network_ref;
    #$t4_time += Time::HiRes::tv_interval($t4);
    #$total_time += Time::HiRes::tv_interval($tt);
  }

  my $total_time = 0;
  my $t1_time = 0;
  my $t2_time = 0;
  my $t3_time = 0;

  print "Processing relationships...\n";
  my $ctr = 0;
  foreach my $network_ref (@network_refs)
  {
    foreach my $node_name (keys %$network_ref)
    {
      my $tt = [Time::HiRes::gettimeofday];
      my $t1 = [Time::HiRes::gettimeofday];
      $ctr++;
      #if($ctr%10 == 0)
      #{
      #  print "\nKeys processed $ctr\n";
      #  print "total: $total_time\n";
      #  print "sum = " . 100*($t1_time + $t2_time + $t3_time)/$total_time . "\n";
      #  print "t1: $t1_time(" . 100*$t1_time/$total_time .")\n";
      #  print "t2: $t2_time(" . 100*$t2_time/$total_time .")\n";
      #  print "t3: $t3_time(" . 100*$t3_time/$total_time .")\n";
      #}
      print "  Samples processed $ctr\n" if $ctr%20000 == 0;
      #print "Node $node_name\n";
      $$network_ref{$node_name}->make_relative_network_from_pedigree($network_ref);
      my $node = $$network_ref{$node_name};
      my %relatives = $node->relatives();

      ## Set relationships
      my %PC;
      my %FS;
      my %HAG;
      my %CGH;

      $t1_time += Time::HiRes::tv_interval($t1);
      my $t2 = [Time::HiRes::gettimeofday];
                      
      ## Get all possible relationships and add them to each of the lists
      foreach my $relative (keys %relatives)
      {
        my $relationship = $relatives{$relative};
        #print "$node_name $relative $relationship\n";
        if ($relationship =~ /^PC$/i)
        {
          $PC{$relative} = $relatives{$relative};
        }
        elsif ($relationship =~ /^FS$/i)
        {
          $FS{$relative} = $relatives{$relative};
        }
        elsif ($relationship =~ /^HAG$/i)
        {
          $HAG{$relative} = $relatives{$relative};
        }
        elsif ($relationship =~ /^CGH$/i)
        {
          $CGH{$relative} = $relatives{$relative};
        }
      }
      $t2_time += Time::HiRes::tv_interval($t2);
      my $t3 = [Time::HiRes::gettimeofday];
      $node->pc(%PC);
      $node->fs(%FS);
      $node->hag(%HAG);
      $node->cgh(%CGH);
      $t3_time += Time::HiRes::tv_interval($t3);
      $total_time += Time::HiRes::tv_interval($tt);
    }
  }
  
	return @network_refs;	
}

