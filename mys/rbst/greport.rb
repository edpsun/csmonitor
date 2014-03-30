require_relative 'rbst'

if ARGV.length == 1
  file = ARGV[0]
else
  file = '/export/tools/hystock/ebk/own.EBK'
end
#puts "[INFO] Using file: #{file}"

all= []
File.open(file) do |file|
  file.each_line do |line|
    if (line.start_with?('16') || line.start_with?('00'))
      retriever = RBST::ReportInfoRetriever.new
      ret = retriever.get_report_info(line[1..-1].strip())
      #puts ret.join " - "
      #print  '.'
      #puts "[#{ret}]"
      all << ret if ret.length > 0
    end
  end
end

puts ''
all.sort! {|x,y| x[2] <=> y[2]}
all.each do|x|
  puts x.join("\t")
end
