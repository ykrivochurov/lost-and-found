angular.module('laf').
  factory('ShareService', function (UtilsService) {
    var share = {
      vkontakte: function (item) {
        var url = 'http://vkontakte.ru/share.php?';
        url += 'url=' + encodeURIComponent(generateUrl(item));
        url += '&title=' + encodeURIComponent(share.abbreviate(item.what, 100, '...'));
        url += '&description=' + encodeURIComponent(item.what + ' \n ' + item.where);
        if (UtilsService.isNotEmpty(item.thumbnailId)) {
          url += '&image=' + encodeURIComponent(thumbUrl(item));
        }
        url += '&noparse=true';
        share.popup(url);
      },
      odnoklassniki: function (purl, text) {
        var url = 'http://www.odnoklassniki.ru/dk?st.cmd=addShare&st.s=1';
        url += '&st.comments=' + encodeURIComponent(text);
        url += '&st._surl=' + encodeURIComponent(purl);
        share.popup(url);
      },
      facebook: function (item) {
        var url = 'http://www.facebook.com/sharer.php?s=100';
        url += '&p[title]=' + encodeURIComponent(share.abbreviate(item.what, 100, '...'));
        url += '&p[summary]=' + encodeURIComponent(item.what + ' \n ' + item.where);
        url += '&p[url]=' + encodeURIComponent(generateUrl(item));
        if (UtilsService.isNotEmpty(item.thumbnailId)) {
          url += '&p[images][0]=' + encodeURIComponent(thumbUrl(item));
        }
        share.popup(url);
      },
      twitter: function (item) {
        var url = 'http://twitter.com/share?';
        url += 'text=' + encodeURIComponent(item.what + ' \n ' + item.where);
        url += '&url=' + encodeURIComponent(generateUrl(item));
        url += '&counturl=' + encodeURIComponent(generateUrl(item));
        share.popup(url);
      },
      mailru: function (purl, ptitle, pimg, text) {
        var url = 'http://connect.mail.ru/share?';
        url += 'url=' + encodeURIComponent(purl);
        url += '&title=' + encodeURIComponent(ptitle);
        url += '&description=' + encodeURIComponent(text);
        url += '&imageurl=' + encodeURIComponent(pimg);
        share.popup(url)
      },

      popup: function (url) {
        window.open(url, '', 'toolbar=0,status=0,width=626,height=436');
      },

      abbreviate: function (str, max, suffix) {
        if ((str = str.replace(/^s+|s+$/g, '').replace(/[rn]*s*[rn]+/g, ' ').replace(/[ t]+/g, ' ')).length <= max) {
          return str;
        }

        var
          abbr = '',
          str = str.split(' '),
          suffix = (typeof suffix !== 'undefined' ? suffix : ' ...'),
          max = (max - suffix.length);

        for (var len = str.length, i = 0; i < len; i++) {
          if ((abbr + str[i]).length < max) {
            abbr += str[i] + ' ';
          }
          else {
            break;
          }
        }

        return abbr.replace(/[ ]$/g, '') + suffix;
      }
    }

    return share;
  });