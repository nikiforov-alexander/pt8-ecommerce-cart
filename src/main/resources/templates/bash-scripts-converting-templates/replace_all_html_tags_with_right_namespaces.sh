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
    replace_head () {
        awk '
            $1 ~ "<html" {
                print "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:th=\"http://www.thymeleaf.org\">"
                getline
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
    replace_head
done
#     end
