require 'erb'
SOURCE =
    %{<% for number in min..max %>
         The number is <%= number %>
      <% end %>
    }
erb = ERB.new(SOURCE)
min = 4
max = 6
puts erb.result(binding)