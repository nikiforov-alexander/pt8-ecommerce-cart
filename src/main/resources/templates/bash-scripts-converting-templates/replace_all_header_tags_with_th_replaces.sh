#!/bin/bash 
set_init_vars () {
    layout="layout.html"
    list_of_excluded_files="layout.html welcome.html about.html"
}
check_if_layout_exists () {
    [ ! -f ${layout} ] && echo 'layout.html is not found' && exit 1
}
#  in cycle
    check_if_file_is_not_excluded () {
        for excluded_file in $list_of_excluded_files ; do
            if [ $html == $excluded_file ] ; then
                echo file $html is excluded
                return 1
            fi
        done
    }
    echo_filename () {
        echo 
        echo file $html:
        echo
    }
    replace_header () {
        awk '
            $1 == "<header>" {
                printf("    <header th:replace=\"layout :: header\">")
                getline
                while ($1 != "</header>") {
                    getline
                }
                getline
                printf("</header>\n")
            }
            { print }
            ' $html > temp
        mv temp $html
    }
#     body
set_init_vars
check_if_layout_exists
for html in `ls *html`; do
    check_if_file_is_not_excluded || continue
    echo_filename 
    replace_header
done
#     end
