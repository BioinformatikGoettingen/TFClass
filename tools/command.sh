while read line
  do 
	export ncbiID=$(echo $line | cut -f1 -d"$")
	export species_name="$(echo $line | cut -f2 -d"$")"
	cat species_template.txt  | sed "s/ncbiID/$ncbiID/g" | sed "s/species_name/$species_name/"
  done <species.txt

