#include <iostream>
#include <string>
using namespace std;

int main (int argc, char** argv)
{
   string line = argv[1];

   string::size_type begIdx, endIdx;

   int slash_pos = 0;

   for (int i=0; i<static_cast<int>(line.length()); i++)
   {
	if (line[i] == '/')
	{
	slash_pos = i;
	}
   }
   //cout << "sl=" << slash_pos << endl;

   for (int j=0; j<static_cast<int>(line.length()); j++)
   {
        if (j > slash_pos)
        {
        cout << line[j];
        }
   }

}
