// echo "yaaaa_"`date | awk '{ print $6 }'`
// write_validate_list 6230.xml


#include <iostream>
#include <fstream>
#include <string>

using std::cerr;
using std::cout;
using std::endl;
using std::getline;
using std::ifstream;
using std::string;

int main(int argc, char **argv)
{


		//command line args expected
		if (argc == 2)
		{

			//open file
			ifstream in(argv[1]);

			if (in) {

				string s;
				bool get_vendor =false;
				int close_par = 0;
				bool vendor_exists = false;

				while (getline(in, s))
				{

					//cout << s.find("prf:Vendor") << endl;

					//look for vendor string
					if (s.find("prf:Vendor") < 1000)
					{
					vendor_exists = true;

						for (int k=0; k < s.length() ; k++)
						{
							if (get_vendor == true && close_par == 1)
							{

								if (s[k] == '<')
								{
									get_vendor =false;
								}
								else
								{
									if(s[k] == ' ')
									{
										cout << '_';
									}
									else
									{
										cout << s[k];
									}
								}

							}

							if (s[k] == '>')
							{
							close_par++;
							get_vendor =true;
							}

						}
					}

				}

                                if (vendor_exists == false)
                                {
                                cout << "VendorNotSpecified";
                                }

				cout << "_UAProf_Validated_List_" ;

			} else {
				cerr << "cannot open file " << argv[1] << endl;
			}

		} else {
		cerr << "write_validate_list uaprof" << endl;
		}

return 0;
}

