require 'rss/maker'
require 'rss/1.0'
require 'rss/2.0'
require 'open-uri'

source = "rss.xml" # url or local file
if File.exist?(source)
  content = "" # raw content of rss feed will be loaded here
  open(source) do |s| content = s.read end
  previous_rss = RSS::Parser.parse(content, false)
end

version = "2.0" # ["0.9", "1.0", "2.0"]
destination = "rss.xml" # local file to write

content = RSS::Maker.make(version) do |m|
  m.channel.title = "Infinitest for Eclipse Releases"
  m.channel.link = "update.improvingworks.com/rss.xml"
  m.channel.description = "News feed for new Infinitest releases"
  m.items.do_sort = true # sort items by date
  
  i = m.items.new_item
  i.title = "Infinitest for Eclipse #{ARGV[0]} released - #{ARGV[1]}"
  i.link = "ReleaseNotes.txt"
  i.date = Time.now
  
  if previous_rss
    previous_rss.items.each do |entry| 
      i = m.items.new_item
      i.title = entry.title
      i.link = entry.link
      i.date = entry.date
    end
  end
end

File.open(destination,"w") do |f|
  f.write(content)
end
