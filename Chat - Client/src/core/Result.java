/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

/**
 *
 * @author APC-LTN
 */
public class Result 
{
    public String actionFlags;
    public String resultFlags;
    public String content;

    public Result(String mActionFlags, String mResultFlags, String mContent) {
        this.actionFlags = mActionFlags;
        this.resultFlags = mResultFlags;
        this.content = mContent;
    }
}
